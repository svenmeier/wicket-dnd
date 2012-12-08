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
package wicketdnd.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Formattable;
import java.util.Formatter;

/**
 * @author Sven Meier
 */
public class CollectionFormattable implements Formattable
{
	private Collection<?> collection;
	
	public CollectionFormattable(Object[] array) {
		this.collection = Arrays.asList(array);
	}
	
	public CollectionFormattable(Collection<?> collection) {
		this.collection = collection;
	}
	
	public void formatTo(Formatter formatter, int flags, int width, int precision)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (Object object : collection) {
			if (builder.length() > 1) {
				builder.append(",");
			}
			if (object == null) {
			} else if (object instanceof Number) {
				builder.append(object.toString());
			} else {
				builder.append("'");
				builder.append(object.toString().replace("'", "\\'"));
				builder.append("'");
			}
		}
		builder.append("]");
		
		formatter.format(builder.toString());
	}
}