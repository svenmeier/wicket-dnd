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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableTreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * @author Sven Meier
 */
public class FooTreeProvider extends SortableTreeProvider<Foo, String>
{

	private List<Foo> foos = new ArrayList<Foo>();

	{
		Foo fooA = new Foo("A");
		foos.add(fooA);
		{
			new Foo(fooA, "AA");
			new Foo(fooA, "AB");
		}
		Foo fooB = new Foo("B");
		foos.add(fooB);
		{
			Foo fooBA = new Foo(fooB, "BA");
			{
				new Foo(fooBA, "BAA");
				new Foo(fooBA, "BAB");
				new Foo(fooBA, "BAC");
				new Foo(fooBA, "BAD");
			}
			new Foo(fooB, "BB");
		}
		Foo fooC = new Foo("C");
		foos.add(fooC);
		{
			new Foo(fooC, "CA");
			new Foo(fooC, "CB");
		}
	}

	public Iterator<? extends Foo> getRoots()
	{
		return foos.iterator();
	}

	public boolean hasChildren(Foo foo)
	{
		return foo.hasChildren();
	}

	public Iterator<? extends Foo> getChildren(Foo foo)
	{
		return foo.getChildren().iterator();
	}

	public IModel<Foo> model(Foo foo)
	{
		return Model.of(foo);
	}

	public void detach()
	{
	}

	public void remove(Foo foo)
	{
		foos.remove(foo);
	}

	public void add(Foo foo)
	{
		foos.add(foo);
	}

	public void add(Foo drag, Foo parent)
	{
		parent.add(drag);
	}

	public void addBefore(Foo drag, Foo drop)
	{
		Foo parent = drop.getParent();
		if (parent == null)
		{
			drag.remove();
			foos.add(foos.indexOf(drop), drag);
		}
		else
		{
			parent.add(drag, parent.indexOf(drop));
		}
	}

	public void addAfter(Foo drag, Foo drop)
	{
		Foo parent = drop.getParent();
		if (parent == null)
		{
			drag.remove();
			foos.add(foos.indexOf(drop) + 1, drag);
		}
		else
		{
			parent.add(drag, parent.indexOf(drop) + 1);
		}
	}
}