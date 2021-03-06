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

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import com.github.ferstl.depgraph.dot.DotBuilder;
import com.github.ferstl.depgraph.dot.NodeRenderer;

/**
 * Aggregates all dependencies of a multi-module project into one single graph.
 */
@Mojo(
    name = "aggregate",
    aggregator = true,
    defaultPhase = LifecyclePhase.NONE,
    inheritByDefault = false,
    requiresDependencyCollection = ResolutionScope.TEST,
    requiresDirectInvocation = false,
    threadSafe = true)
public class AggregatingDependencyGraphMojo extends AbstractGraphMojo {

  /**
   * If set to {@code true}, the created graph will show the {@code groupId} on all artifacts.
   * @since 1.0.3
   */
  @Parameter(property = "showGroupIds", defaultValue = "false")
  boolean showGroupIds;

  /**
   * If set to {@code true} the artifact nodes will show version information.
   *
   * @since 1.0.0
   */
  @Parameter(property = "showVersions", defaultValue = "false")
  boolean showVersions;

  /**
   * If set to {@code true}, all parent modules (&lt;packaging&gt;pom&lt;/packaging&gt) will be shown with a dotted
   * arrow pointing to their child modules.
   *
   * @since 1.0.0
   */
  @Parameter(property = "includeParentProjects", defaultValue = "false")
  private boolean includeParentProjects;

  @Override
  protected GraphFactory createGraphFactory(ArtifactFilter artifactFilter) {
    DotBuilder dotBuilder = new DotBuilder()
      .useNodeRenderer(NodeRenderers.VERSIONLESS_ID)
      .useNodeLabelRenderer(determineNodeLabelRenderer());

    GraphBuilderAdapter adapter = new GraphBuilderAdapter(this.dependencyGraphBuilder);

    return new AggregatingGraphFactory(adapter, artifactFilter, dotBuilder, this.includeParentProjects);
  }

  private NodeRenderer determineNodeLabelRenderer() {
    NodeRenderer renderer = NodeRenderers.ARTIFACT_ID_LABEL;

    if (this.showGroupIds && this.showVersions) {
      renderer = NodeRenderers.GROUP_ID_ARTIFACT_ID_VERSION_LABEL;
    } else if (this.showVersions) {
      renderer = NodeRenderers.ARTIFACT_ID_VERSION_LABEL;
    } else if (this.showGroupIds) {
      renderer = NodeRenderers.GROUP_ID_ARTIFACT_ID_LABEL;
    }

    return renderer;
  }
}
