/*
 * Copyright 2009 Sven Meier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicketdnd.examples;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sven Meier
 */
public class Foo implements Serializable
{

	private String name;

	private Foo parent;

	private List<Foo> children = new ArrayList<Foo>();

	public Foo(String name)
	{
		this.name = name;
	}

	public Foo(Foo parent, String name)
	{
		this.name = name;
		
		parent.add(this);
	}

	public String getName()
	{
		return name;
	}

	public Foo getParent()
	{
		return parent;
	}
	
	public boolean hasChildren()
	{
		return !children.isEmpty();
	}

	public List<Foo> getChildren()
	{
		return children;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public void remove()
	{
		if (parent != null) {
			parent.children.remove(this);
			parent = null;
		}
	}

	public void add(Foo foo) {
		add(foo, children.size());
	}
	
	public void add(Foo foo, int index)
	{
		foo.remove();
		
		foo.parent = this;
		children.add(index, foo);
	}

	public Foo copy()
	{
		Foo copy = new Foo(this.name);
		
		for (Foo child : children) {
			copy.add(child.copy());
		}
		
		return copy;
	}

	public Foo link()
	{
		return new Foo("^" + this.name);
	}

	public int indexOf(Foo child)
	{
		return children.indexOf(child);
	}

	public boolean isAncestor(Object foo)
	{
		if (parent == foo) {
			return true;
		}
		
		if (parent == null) {
			return false;
		} else {
			return parent.isAncestor(foo);
		}
	}
}