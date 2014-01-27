/*
 * Copyright (c) 2014 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evolveum.midpoint.prism.xnode;

import javax.xml.namespace.QName;

import com.evolveum.midpoint.prism.PrismPropertyDefinition;
import com.evolveum.midpoint.util.DebugUtil;
import com.evolveum.midpoint.util.PrettyPrinter;
import com.evolveum.midpoint.util.exception.SchemaException;

public class PrimitiveXNode<T> extends XNode {
	
	private T value;
	private ValueParser<T> valueParser;
	
	public void parseValue(QName typeName) throws SchemaException {
		if (valueParser != null) {
			value = valueParser.parse(typeName);
		}
	}
	
	public T getValue() {
		return value;
	}

	public T getParsedValue(QName typeName) throws SchemaException {
		if (!isParsed()) {
			parseValue(typeName);
		}
		return value;
	}
	
	public ValueParser<T> getValueParser() {
		return valueParser;
	}

	public void setValueParser(ValueParser<T> valueParser) {
		this.valueParser = valueParser;
	}

	public void setValue(T value) {
		this.value = value;
	}
	
	public boolean isParsed() {
		return value != null;
	}

	
	/**
	 * Returns a value that is correctly string-formatted according
	 * to its type definition. Works properly only if definition is set.
	 */
//	public String getValueAsString() {
//		
//	}
	
	@Override
	public String debugDump(int indent) {
		StringBuilder sb = new StringBuilder();
		DebugUtil.indentDebugDump(sb, indent);
		valueToString(sb);
		String dumpSuffix = dumpSuffix();
		if (dumpSuffix != null) {
			sb.append(dumpSuffix);
		}
		return sb.toString();
	}
	
	@Override
	public String getDesc() {
		return "primitive";
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("XNode(primitive:");
		valueToString(sb);
		sb.append(")");
		return sb.toString();
	}

	private void valueToString(StringBuilder sb) {
		if (value == null) {
			sb.append("parser ").append(valueParser);
		} else {
			sb.append(PrettyPrinter.prettyPrint(value));
		}
	}

}