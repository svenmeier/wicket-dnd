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

/**
 * @author Sven Meier
 */
public class FooList extends ArrayList<Foo>
{

	{
		add(new Foo("A"));
		add(new Foo("B"));
		add(new Foo("C"));
	}
	
	public void addBefore(Foo drag, Foo drop)
	{
		drag.remove();
		add(indexOf(drop), drag);
	}
	
	public void addAfter(Foo drag, Foo drop)
	{
		drag.remove();
		add(indexOf(drop) + 1, drag);
	}
}