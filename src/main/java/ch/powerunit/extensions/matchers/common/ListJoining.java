/**
 * Powerunit - A JDK1.8 test framework
 * Copyright (C) 2014 Mathieu Boretti.
 *
 * This file is part of Powerunit
 *
 * Powerunit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Powerunit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Powerunit. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.powerunit.extensions.matchers.common;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import ch.powerunit.extensions.matchers.common.lang.ListJoiningAround;
import ch.powerunit.extensions.matchers.common.lang.ListJoiningDelimiter;
import ch.powerunit.extensions.matchers.common.lang.ListJoiningMapper;

/**
 * @author borettim
 *
 */
public final class ListJoining<E> {

	public static final ListJoining<Object> COMMA_SEPARATED = accepting(Object.class).withToStringMapper()
			.withCommaDelimiter().withoutSuffixAndPrefix();

	public static final ListJoining<Object> NL_SEPARATED = accepting(Object.class).withToStringMapper()
			.withDelimiter("\n").withoutSuffixAndPrefix();

	private final Function<E, String> mapper;

	private final String delimiter;

	private final UnaryOperator<String> finalize;

	public static class Builder<E> implements ListJoiningAround<E>, ListJoiningDelimiter<E>, ListJoiningMapper<E> {

		private Function<E, String> mapper;

		private String delimiter;

		@Override
		public ListJoiningDelimiter<E> withMapper(Function<E, String> mapper) {
			this.mapper = Objects.requireNonNull(mapper, "mapper can't be null");
			return this;
		}

		@Override
		public ListJoiningAround<E> withDelimiter(String delimiter) {
			this.delimiter = Objects.requireNonNull(delimiter, "delimiter can't be null");
			return this;
		}

		@Override
		public ListJoining<E> withFinalFunction(UnaryOperator<String> finalize) {
			return new ListJoining<E>(mapper, delimiter, finalize);
		}

	}
	
	public static <E> ListJoining<E> nlSeparated() {
		return (ListJoining<E>) NL_SEPARATED;
	}
	
	public static <E> ListJoining<E> commaSeparated() {
		return (ListJoining<E>) COMMA_SEPARATED;
	}

	public static <E> ListJoiningMapper<E> accepting(Class<E> clazz) {
		return new Builder<E>();
	}

	public static <E> ListJoiningDelimiter<E> joinWithMapper(Function<E, String> mapper) {
		return new Builder<E>().withMapper(mapper);
	}

	public static <E> ListJoining<E> joinWithMapperAndDelimiter(Function<E, String> mapper, String delimiter) {
		return new Builder<E>().withMapper(mapper).withDelimiter(delimiter).withoutSuffixAndPrefix();
	}

	public ListJoining(Function<E, String> mapper, String delimiter, UnaryOperator<String> finalize) {
		this.mapper = mapper;
		this.delimiter = delimiter;
		this.finalize = finalize;
	}

	public String asString(List<E> input) {
		return Objects.requireNonNull(input, "input can't be null").stream().map(mapper)
				.collect(collectingAndThen(joining(delimiter), finalize));
	}

	public String asString(E... input) {
		return asString(Arrays.asList(input));
	}
}
