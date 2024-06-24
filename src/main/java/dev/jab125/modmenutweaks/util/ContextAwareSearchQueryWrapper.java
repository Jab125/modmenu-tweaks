package dev.jab125.modmenutweaks.util;

import java.util.Arrays;

/**
 * Start and end means that a substring should return "@query"
 * @param query
 * @param start
 * @param end
 */
public record ContextAwareSearchQueryWrapper(SearchQuery query, int start, int end) {
	@Override
	public String toString() {
		return "ContextAwareSearchQueryWrapper{" +
			   "query=" + Arrays.toString(query.filterNames()) +
			   ", start=" + start +
			   ", end=" + end +
			   '}';
	}
}
