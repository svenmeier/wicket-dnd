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

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * @author Sven Meier
 */
public class FooDataProvider implements ISortableDataProvider<Foo, String>
{

	private List<Foo> foos = new ArrayList<Foo>();

	{
		foos.add(new Foo("A"));
		foos.add(new Foo("B"));
		foos.add(new Foo("C"));
		foos.add(new Foo("D"));
		foos.add(new Foo("E"));
		foos.add(new Foo("F"));
		foos.add(new Foo("G"));
		foos.add(new Foo("H"));
		foos.add(new Foo("I"));
		foos.add(new Foo("J"));
	}

	public long size()
	{
		return foos.size();
	}

	public Iterator<? extends Foo> iterator(long first, long count)
	{
		return foos.subList((int)first, (int)(first + count)).iterator();
	}

	public IModel<Foo> model(Foo foo)
	{
		return Model.of(foo);
	}

	public ISortState<String> getSortState()
	{
		return new SingleSortState<String>();
	}

	public void setSortState(ISortState<String> state)
	{
	}

	public void detach()
	{
	}

	public void remove(Foo foo)
	{
		foos.remove(foo);
	}

	public void add(Foo drag)
	{
		foos.add(drag);
	}

	public void addBefore(Foo drag, Foo drop)
	{
		drag.remove();
		foos.add(foos.indexOf(drop), drag);
	}

	public void addAfter(Foo drag, Foo drop)
	{
		drag.remove();
		foos.add(foos.indexOf(drop) + 1, drag);
	}
}