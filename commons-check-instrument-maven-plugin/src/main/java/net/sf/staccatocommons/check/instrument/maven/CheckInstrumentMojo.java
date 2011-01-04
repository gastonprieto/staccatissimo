/*
 Copyright (c) 2010, The Staccato-Commons Team

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation; version 3 of the License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
 */
package net.sf.staccatocommons.check.instrument.maven;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.sf.staccato.commons.check.instrument.CheckConfigurer;
import net.sf.staccato.commons.collections.stream.Streams;
import net.sf.staccato.commons.instrument.InstrumentationRunner;
import net.sf.staccato.commons.io.Directory;
import net.sf.staccato.commons.lang.SoftException;
import net.sf.staccato.commons.lang.function.Function;
import net.sf.staccato.commons.lang.predicate.Predicates;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author flbulgarelli
 * @goal instrument
 */
public class CheckInstrumentMojo extends AbstractMojo {

	/**
	 * The location to instrument. It is <project directory>/target/classes by
	 * default
	 * 
	 * @required
	 * @parameter expression="${instrument.location}"
	 *            default-value="${project.build.directory}/classes"
	 */
	private String location;

	/**
	 * @readonly
	 * @required
	 * @parameter expression="${plugin.artifacts}"
	 */
	protected List<Artifact> pluginArtifactsList;

	/**
	 * If check annotation on methods should be ignored, or should be processed
	 * with assertion checks on their return types
	 * 
	 * @parameter default-value="true"
	 */
	private boolean ignoreReturnChecks;

	/**
	 * The current artifact being instrumented. The mojo will normally not need
	 * this object, except on those projects from staccato-commons that are both
	 * dependencies of this mojo and consumers of it - like staccato-commons-lang,
	 * io, etc
	 * 
	 * @readonly
	 * @required
	 * @parameter default-value="${project.artifact}"
	 */
	private Artifact artifact;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Instrumenting checks to classes located in " + location);
		String extraClasspath = createClassPathString();
		getLog().debug("Using classpath " + extraClasspath);
		try {
			InstrumentationRunner.runInstrumentation(//
				new CheckConfigurer(ignoreReturnChecks),
				new Directory(location),
				extraClasspath);
		} catch (Exception e) {
			getLog().error(e.getMessage());
			throw new MojoExecutionException("Unexpected error", e);
		}
		getLog().info("Checks instrumented sucessfully");
	}

	private String createClassPathString() {
		return Streams //
			.from(pluginArtifactsList)
			.filter(Predicates.equal(artifact).not())
			.map(new Function<Artifact, String>() {
				public String apply(Artifact arg) {
					try {
						return arg.getFile().getCanonicalPath();
					} catch (IOException e) {
						throw SoftException.soften(e);
					}
				}
			})
			.joinStrings(File.pathSeparator);
	}
}
