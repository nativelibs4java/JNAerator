/*
	Copyright (c) 2009-2011 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU Lesser General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public License for more details.
	
	You should have received a copy of the GNU Lesser General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.ochafik.lang.jnaerator.parser;

import java.util.ArrayList;
import java.util.List;

import com.ochafik.lang.jnaerator.parser.TypeRef.SimpleTypeRef;
import com.ochafik.lang.jnaerator.parser.TypeRef.TaggedTypeRef;
import java.util.Arrays;
import java.util.Collections;

public class Enum extends TaggedTypeRef {
	Type type = Type.C;

    public static enum Type {
        C, Java
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static class EnumItem extends Element {
		String name;
		List<Expression> arguments = new ArrayList<Expression>();
        Struct body;
        Type type;

        public EnumItem() {
			super();
		}

        public Type getType() {
            if (type == null && (getParentElement() instanceof Enum))
                return ((Enum)getParentElement()).getType();
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

		public EnumItem(String name, Expression... arguments) {
			setName(name);
			setArguments(Arrays.asList(arguments));
		}

        public Struct getBody() {
            return body;
        }

        public void setBody(Struct body) {
            this.body = changeValue(this, this.body, body);
        }


		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public List<Expression> getArguments() {
			return Collections.unmodifiableList(arguments);
		}
		public void setArguments(List<Expression> arguments) {
			changeValue(this, this.arguments, arguments);
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visitEnumItem(this);
		}

		@Override
		public Element getNextChild(Element child) {
            Element e = getNextSibling(arguments, child);
            if (e != null)
                return e;
			return null;
		}

		@Override
		public Element getPreviousChild(Element child) {
			Element e = getPreviousSibling(arguments, child);
            if (e != null)
                return e;
			return null;
		}

		@Override
		public boolean replaceChild(Element child, Element by) {
			if (replaceChild(arguments, Expression.class, this, child, by))
                return true;
            
            if (child == getBody()) {
				setBody((Struct) by);
				return true;
			}
			return super.replaceChild(child, by);
		}
		
	}
	final List<EnumItem> items = new ArrayList<EnumItem>();
    final List<Identifier> interfaces = new ArrayList<Identifier>();

    Struct body;
	//private LinkedHashMap<String, Expression> values = new LinkedHashMap<String, Expression>();
	//Integer lastValue = 0;
	
	public void addItem(EnumItem item) {
		if (item == null)
			return;
		
		item.setParentElement(this);
        items.add(item);
	}

	public void accept(Visitor visitor) {
		visitor.visitEnum(this);
	}
	
	public List<EnumItem> getItems() {
		return unmodifiableList(items);
	}
	public void setItems(List<EnumItem> items) {
		changeValue(this, this.items, items);
	}
	@Override
	public Element getNextChild(Element child) {
		Element e = super.getNextChild(child);
		if (e != null)
			return e;
		e = getNextSibling(items, child);
		if (e != null)
			return e;
        e = getNextSibling(interfaces, child);
		if (e != null)
			return e;

		return super.getNextChild(child);
	}

	@Override
	public Element getPreviousChild(Element child) {
		Element e = super.getPreviousChild(child);
		if (e != null)
			return e;
		e = getPreviousSibling(items, child);
		if (e != null)
			return e;
        e = getPreviousSibling(interfaces, child);
		if (e != null)
			return e;

		return super.getPreviousChild(child);
	}

    public Struct getBody() {
        return body;
    }

    public void setBody(Struct body) {
        this.body = changeValue(this, this.body, body);
    }

    public List<Identifier> getInterfaces() {
        return Collections.unmodifiableList(interfaces);
    }

    public void setInterfaces(List<Identifier> interfaces) {
        changeValue(this, this.interfaces, interfaces);
    }
    public void addInterface(Identifier interf) {
		if (interf == null)
			return;
		interf.setParentElement(this);
		interfaces.add(interf);
	}
	

	@Override
	public boolean replaceChild(Element child, Element by) {
		if (super.replaceChild(child, by))
			return true;
		
		if (replaceChild(items, EnumItem.class, this, child, by))
			return true;
		if (replaceChild(interfaces, Identifier.class, this, child, by))
			return true;

        if (child == getBody()) {
            setBody((Struct) by);
            return true;
        }
        return super.replaceChild(child, by);
	}
}
