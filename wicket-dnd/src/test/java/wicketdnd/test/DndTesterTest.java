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
package wicketdnd.test;

import junit.framework.TestCase;

import org.apache.wicket.util.tester.WicketTester;

import wicketdnd.Anchor;
import wicketdnd.Location;
import wicketdnd.Operation;

/**
 * Test for {@link DnDTester}.
 */
public class DndTesterTest extends TestCase
{

	private WicketTester tester = new WicketTester();

	public void testDrag()
	{
		TestPage page = new TestPage(Operation.COPY);

		tester.startPage(page);

		new DnDTester(tester).executeDrag(page.dropTarget, new Location(page.drop, Anchor.CENTER));

		assertEquals(1, page.log.size());
		assertEquals("onDrag", page.log.get(0));
	}

	public void testDrop()
	{
		TestPage page = new TestPage(Operation.COPY);

		tester.startPage(page);

		new DnDTester(tester).executeDrop(page.dropTarget, new Location(page.drop, Anchor.CENTER),
				Operation.COPY, page.dragSource, page.drag);

		assertEquals(3, page.log.size());
		assertEquals("onBeforeDrop", page.log.get(0));
		assertEquals("onDrop", page.log.get(1));
		assertEquals("onAfterDrop", page.log.get(2));
	}

	public void testDropRejected()
	{
		TestPage page = new TestPage();

		tester.startPage(page);

		new DnDTester(tester).executeDrop(page.dropTarget, new Location(page.drop, Anchor.CENTER),
				Operation.COPY, page.dragSource, page.drag);

		assertEquals(1, page.log.size());
		assertEquals("onRejected", page.log.get(0));
	}
}
