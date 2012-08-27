package com.evolveum.midpoint.repo.sql.query;

import javax.xml.namespace.QName;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Element;

import com.evolveum.midpoint.prism.PrismReferenceValue;
import com.evolveum.midpoint.prism.query.ObjectFilter;
import com.evolveum.midpoint.prism.query.OrgFilter;
import com.evolveum.midpoint.schema.SchemaConstantsGenerated;
import com.evolveum.midpoint.util.DOMUtil;

public class TreeOp extends Op {

	private static final String QUERY_PATH = "descendants";
	private static final String CLOSURE_ALIAS = "closure";
	private static final String ANCESTOR = CLOSURE_ALIAS + ".ancestor";
	private static final String ANCESTOR_ALIAS = "anc";
	private static final String ANCESTOR_OID = ANCESTOR_ALIAS + ".oid";
	private static final String DEPTH = CLOSURE_ALIAS + ".depth";

	public TreeOp(QueryInterpreter interpreter) {
		super(interpreter);
		// TODO Auto-generated constructor stub
	}

	// TODO: implement
	public Criterion interpret(ObjectFilter filter, boolean pushNot) throws QueryException {

		updateCriteria();

		OrgFilter org = null;
		if (filter instanceof OrgFilter) {
			org = (OrgFilter) filter;
		}

		if (org.getOrgRef() == null) {
			throw new QueryException("No organization reference defined in the search query.");
		}

		if (org.getOrgRef().getOid() == null) {
			throw new QueryException("No oid specified in organization refernece " + org.getOrgRef().dump());
		}

		String orgRefOid = org.getOrgRef().getOid();

		Integer maxDepth = null;
		if (org.getMaxDepth() != null && !("unbounded").equals(org.getMaxDepth())) {
			try {
				maxDepth = Integer.valueOf(org.getMaxDepth());
			} catch (NumberFormatException ex) {
				throw new QueryException("Bad value specified for max depth in search query. Expected integer value.");
			}
		}

		if (maxDepth == null) {
			return Restrictions.eq(ANCESTOR_OID, orgRefOid);
		} else {
			return Restrictions.and(Restrictions.eq(ANCESTOR_OID, orgRefOid), Restrictions.lt(DEPTH, maxDepth));
		}

	}

	private void updateCriteria() {
		// get root criteria
		Criteria pCriteria = getInterpreter().getCriteria(null);
		// create subcriteria on the ROgrClosure table to search through org
		// struct
		pCriteria.createCriteria(QUERY_PATH, CLOSURE_ALIAS).setFetchMode(ANCESTOR, FetchMode.DEFAULT)
				.createAlias(ANCESTOR, ANCESTOR_ALIAS);

	}
}
