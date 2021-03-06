package com.liferay.cli.project;

import static com.liferay.cli.support.util.FileUtils.CURRENT_DIRECTORY;

import com.liferay.cli.file.monitor.event.FileDetails;
import com.liferay.cli.support.osgi.OSGiUtils;
import com.liferay.cli.support.util.FileUtils;

import java.io.File;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.felix.scr.annotations.Component;
import org.osgi.service.component.ComponentContext;

/**
 * Convenient superclass for {@link PathResolvingStrategy} implementations.
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
@Component(componentAbstract = true)
public abstract class AbstractPathResolvingStrategy implements
        PathResolvingStrategy {

    protected static final String ROOT_MODULE = "";

    private String rootPath;

    // ------------ OSGi component methods ----------------

    protected void activate(final ComponentContext context) {
        final File projectDirectory = new File(StringUtils.defaultIfEmpty(
                OSGiUtils.getRayWorkingDirectory(context), CURRENT_DIRECTORY));
        rootPath = FileUtils.getCanonicalPath(projectDirectory);
    }

    // ------------ PathResolvingStrategy methods ----------------

    protected abstract PhysicalPath getApplicablePhysicalPath(String identifier);

    public String getFriendlyName(final String identifier) {
        Validate.notNull(identifier, "Identifier required");
        final LogicalPath p = getPath(identifier);
        if (p == null) {
            return identifier;
        }
        return p.getName() + getRelativeSegment(identifier);
    }

    public LogicalPath getPath(final String identifier) {
        final PhysicalPath parent = getApplicablePhysicalPath(identifier);
        if (parent == null) {
            return null;
        }
        return parent.getLogicalPath();
    }

    public Collection<LogicalPath> getPaths() {
        return getPaths(false);
    }

    /**
     * Obtains the {@link Path}s.
     *
     * @param requireSource <code>true</code> to return only paths containing
     *            Java source code, or <code>false</code> to return all paths
     * @return the matching paths (never <code>null</code>)
     */
    protected abstract Collection<LogicalPath> getPaths(boolean sourceOnly);

    public String getRelativeSegment(final String identifier) {
        final PhysicalPath parent = getApplicablePhysicalPath(identifier);
        if (parent == null) {
            return null;
        }
        final FileDetails parentFile = new FileDetails(parent.getLocation(),
                null);
        return parentFile.getRelativeSegment(identifier);
    }

    public String getRoot() {
        return rootPath;
    }

    public Collection<LogicalPath> getSourcePaths() {
        return getPaths(true);
    }
}
