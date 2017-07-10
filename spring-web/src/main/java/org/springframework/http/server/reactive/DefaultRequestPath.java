/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.http.server.reactive;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Default implementation of {@link RequestPath}.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
class DefaultRequestPath implements RequestPath {

	private final PathContainer fullPath;

	private final PathContainer contextPath;

	private final PathContainer pathWithinApplication;


	DefaultRequestPath(URI uri, @Nullable String contextPath) {
		this.fullPath = PathContainer.parse(uri.getRawPath(), StandardCharsets.UTF_8);
		this.contextPath = initContextPath(this.fullPath, contextPath);
		this.pathWithinApplication = extractPathWithinApplication(this.fullPath, this.contextPath);
	}

	DefaultRequestPath(RequestPath requestPath, @Nullable String contextPath) {
		this.fullPath = requestPath;
		this.contextPath = initContextPath(this.fullPath, contextPath);
		this.pathWithinApplication = extractPathWithinApplication(this.fullPath, this.contextPath);
	}

	private static PathContainer initContextPath(PathContainer path, @Nullable String contextPath) {
		if (!StringUtils.hasText(contextPath) || "/".equals(contextPath)) {
			return PathContainer.parse("", StandardCharsets.UTF_8);
		}

		Assert.isTrue(contextPath.startsWith("/") && !contextPath.endsWith("/") &&
				path.value().startsWith(contextPath), "Invalid contextPath: " + contextPath);

		int length = contextPath.length();
		int counter = 0;

		for (int i=0; i < path.elements().size(); i++) {
			PathContainer.Element element = path.elements().get(i);
			counter += element.value().length();
			if (element instanceof PathContainer.Segment) {
				counter += ((Segment) element).semicolonContent().length();
			}
			if (length == counter) {
				return DefaultPathContainer.subPath(path, 0, i + 1);
			}
		}

		// Should not happen..
		throw new IllegalStateException("Failed to initialize contextPath='" + contextPath + "'" +
				" given path='" + path.value() + "'");
	}

	private static PathContainer extractPathWithinApplication(PathContainer fullPath, PathContainer contextPath) {
		return PathContainer.subPath(fullPath, contextPath.elements().size());
	}


	// PathContainer methods..

	@Override
	public String value() {
		return this.fullPath.value();
	}

	@Override
	public List<Element> elements() {
		return this.fullPath.elements();
	}


	// RequestPath methods..

	@Override
	public PathContainer contextPath() {
		return this.contextPath;
	}

	@Override
	public PathContainer pathWithinApplication() {
		return this.pathWithinApplication;
	}


	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		DefaultRequestPath that = (DefaultRequestPath) other;
		return (this.fullPath.equals(that.fullPath) &&
				this.contextPath.equals(that.contextPath) &&
				this.pathWithinApplication.equals(that.pathWithinApplication));
	}

	@Override
	public int hashCode() {
		int result = this.fullPath.hashCode();
		result = 31 * result + this.contextPath.hashCode();
		result = 31 * result + this.pathWithinApplication.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "DefaultRequestPath[fullPath='" + this.fullPath + "', " +
				"contextPath='" + this.contextPath.value() + "', " +
				"pathWithinApplication='" + this.pathWithinApplication.value() + "']";
	}

}
