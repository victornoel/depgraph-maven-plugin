/*
 * Copyright (c) 2014 by Stefan Ferstl <st.ferstl@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ferstl.depgraph;

import org.apache.maven.artifact.Artifact;

import com.github.ferstl.depgraph.dot.Node;

/**
 * {@link Node} implementation that adapts to:
 * <ul>
 * <li>{@link org.apache.maven.artifact.Artifact}</li>
 * <li>{@link org.apache.maven.shared.dependency.graph.DependencyNode}</li>
 * <li>{@link org.apache.maven.shared.dependency.tree.DependencyNode}</li>
 * </ul>
 */
public class DependencyNodeAdapter implements Node {

  private final Artifact artifact;
  private final NodeResolution resolution;


  public  DependencyNodeAdapter(Artifact artifact) {
    this(artifact, NodeResolution.INCLUDED);
  }

  public DependencyNodeAdapter(org.apache.maven.shared.dependency.graph.DependencyNode dependencyNode) {
    this(dependencyNode.getArtifact());
  }

  public DependencyNodeAdapter(org.apache.maven.shared.dependency.tree.DependencyNode dependencyNode) {
    this(dependencyNode.getArtifact(), determineResolution(dependencyNode.getState()));
  }

  private DependencyNodeAdapter(Artifact artifact, NodeResolution resolution) {
    if (artifact == null) {
      throw new NullPointerException("Artifact must not be null");
    }

    // FIXME: better create a copy of the artifact and set the scope there.
    if (artifact.getScope() == null) {
      artifact.setScope("compile");
    }

    this.artifact = artifact;
    this.resolution = resolution;
  }

  @Override
  public Artifact getArtifact() {
    return this.artifact;
  }

  @Override
  public NodeResolution getResolution() {
    return this.resolution;
  }

  private static NodeResolution determineResolution(int res) {
    switch (res) {
      case org.apache.maven.shared.dependency.tree.DependencyNode.OMITTED_FOR_DUPLICATE:
        return NodeResolution.OMITTED_FOR_DUPLICATE;
      case org.apache.maven.shared.dependency.tree.DependencyNode.OMITTED_FOR_CONFLICT:
        return NodeResolution.OMITTED_FOR_CONFLICT;
      case org.apache.maven.shared.dependency.tree.DependencyNode.OMITTED_FOR_CYCLE:
        return NodeResolution.OMITTED_FOR_CYCLE;
      default:
        return NodeResolution.INCLUDED;
    }
  }
}
