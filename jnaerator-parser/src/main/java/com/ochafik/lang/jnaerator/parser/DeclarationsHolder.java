/*
	Copyright (c) 2009-2011 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (https://github.com/nativelibs4java/JNAerator).
	
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

import java.util.Collections;
import java.util.List;

public interface DeclarationsHolder {
	void addDeclaration(Declaration d);
	List<Declaration> getDeclarations();
	Identifier getResolvedJavaIdentifier();

    /** @return any proxied holder for identity comparison, or this if there's no proxied holder. */
    DeclarationsHolder resolveHolder();
	
	public static class ListWrapper implements DeclarationsHolder {
		public ListWrapper(List<Declaration> list) {
			this.list = list;
		}

		List<Declaration> list;

		@Override
        public void addDeclaration(Declaration d) {
			list.add(d);
		}

		@Override
        public List<Declaration> getDeclarations() {
			return Collections.unmodifiableList(list);
		}

        @Override
        public Identifier getResolvedJavaIdentifier() {
            return null;
        }

        @Override
        public DeclarationsHolder resolveHolder() {
            return this;
        }
	}
}
