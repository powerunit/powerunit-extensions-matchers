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

/**
 * Annotations to be used to generate <i>hamcrest</i> Matchers based on this
 * annotation provider.
 * <hr>
 * <p>
 * <b>Metadata</b>
 * <p>
 * Since version 0.2.0, a metadata section is part of the generated matcher. The
 * format of these metadatas may change and is considered as an internal feature
 * and not a public feature, but this can be used for debugging and this used by
 * the annotation processor itself to detect the compatibility of existing
 * matchers.
 * <p>
 * The essential attribute is the
 * <code>COMPATIBILITY</code> attribute of the class <code>Metadata</code>
 * (referenced from the static field <code>METADATA</code>) which exposes an
 * encoded long with the compatibility information regarding this matcher.
 * <p>
 * The <code>ANNOTATION_PROCESSOR_VERSION </code> may be used to find the
 * version of the annotation processor used to generate the class.
 * <p>
 * <b>Module</b>
 * <p>
 * Since version 1.0.0, the main public package is <code>ch.powerunit.extensions.matchers.api</code> and
 * not any more <code>ch.powerunit.extensions.matchers</code>.
 * 
 * @author borettim
 * 
 * @see ch.powerunit.extensions.matchers.api.ProvideMatchers
 *      <code>@ProvideMatchers</code>&nbsp;- the annotation to be used on class
 *      that must be processed by this annotation processor.
 *
 * @since 1.0.0
 */
package ch.powerunit.extensions.matchers.api;