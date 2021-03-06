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

import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;

import com.github.ferstl.depgraph.dot.DotBuilder;
import com.github.ferstl.depgraph.dot.Node;


/**
 * A node visitor that creates edges between the visited nodes using a {@link DotBuilder}. This
 * class implements the {@code DependencyNodeVisitor} interfaces for dependency trees and dependency
 * graphs and adapts the different node instances using {@link DependencyNodeAdapter}.
 */
class DotBuildingVisitor implements org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor, org.apache.maven.shared.dependency.tree.traversal.DependencyNodeVisitor {

  private final DotBuilder dotBuilder;
  private final Deque<Node> stack;
  private final ArtifactFilter artifactFilter;


  public DotBuildingVisitor(DotBuilder dotBuilder, ArtifactFilter artifactFilter) {
    this.dotBuilder = dotBuilder;
    this.stack = new ArrayDeque<>();
    this.artifactFilter = artifactFilter;
  }

  public DotBuildingVisitor(DotBuilder dotBuilder) {
    this(dotBuilder, DoNothingArtifactFilter.INSTANCE);
  }

  @Override
  public boolean visit(org.apache.maven.shared.dependency.graph.DependencyNode node) {
    return internalVisit(new DependencyNodeAdapter(node));
  }

  @Override
  public boolean endVisit(org.apache.maven.shared.dependency.graph.DependencyNode node) {
    return internalEndVisit(new DependencyNodeAdapter(node));
  }

  @Override
  public boolean visit(org.apache.maven.shared.dependency.tree.DependencyNode node) {
    return internalVisit(new DependencyNodeAdapter(node));
  }

  @Override
  public boolean endVisit(org.apache.maven.shared.dependency.tree.DependencyNode node) {
    return internalEndVisit(new DependencyNodeAdapter(node));
  }

  private boolean internalVisit(Node node) {
    Node currentParent = this.stack.peek();

    if (this.artifactFilter.include(node.getArtifact())) {
      if (currentParent != null) {
        this.dotBuilder.addEdge(currentParent, node);
      }

      this.stack.push(node);

      return true;
    }

    return false;
  }

  private boolean internalEndVisit(Node node) {
    if (this.artifactFilter.include(node.getArtifact())) {
      this.stack.pop();
    }

    return true;
  }

  private static enum DoNothingArtifactFilter implements ArtifactFilter {
    INSTANCE;

    @Override
    public boolean include(Artifact artifact) {
      return true;
    }

  }
}
