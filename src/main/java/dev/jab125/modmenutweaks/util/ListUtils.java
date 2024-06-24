package dev.jab125.modmenutweaks.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class ListUtils {
	public static <T> Iterable<T> reversed(List<T> queries) {
		return new Iterable<T>() {
			@NotNull
			@Override
			public Iterator<T> iterator() {
				return new Iterator<>() {
					private int cursor = queries.size();

					@Override
					public boolean hasNext() {
						return cursor > 0;
					}

					@Override
					public T next() {
						cursor--;
						return queries.get(cursor);
					}
				};
			}
		};
	}
}
