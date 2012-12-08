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
package wicketdnd;

import java.util.EnumSet;
import java.util.Set;

/**
 * An operation of a {@link Transfer}.
 * 
 * @author Sven Meier
 * 
 * @see Transfer#getOperation()
 * @see DragSource#getOperations()
 * @see DropTarget#getOperations()
 */
public enum Operation {
	
	MOVE, COPY, LINK;
	
	/**
	 * Create a set of operations.
	 * 
	 * @param operations operations
	 */
	public static Set<Operation> of(Operation... operations)
	{
		Set<Operation> set = EnumSet.noneOf(Operation.class);
		for (Operation operation : operations) {
			set.add(operation);
		}
		return set;
	}
}