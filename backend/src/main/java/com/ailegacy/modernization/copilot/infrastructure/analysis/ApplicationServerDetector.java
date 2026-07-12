package com.ailegacy.modernization.copilot.infrastructure.analysis;

import com.ailegacy.modernization.copilot.infrastructure.analysis.model.ScannedFile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Detects the target application server from vendor-specific deployment
 * descriptors, falling back to a generic servlet container when only a plain
 * web.xml is present.
 */
@Component
public class ApplicationServerDetector {

    public String detect(List<ScannedFile> files) {
        if (hasFile(files, "weblogic.xml") || hasFile(files, "weblogic-application.xml")) {
            return "Oracle WebLogic";
        }
        if (hasFile(files, "jboss-web.xml") || hasFile(files, "jboss-deployment-structure.xml")) {
            return "JBoss / WildFly";
        }
        if (hasFile(files, "ibm-web-ext.xml") || hasFile(files, "ibm-web-bnd.xml")) {
            return "IBM WebSphere";
        }
        if (hasFile(files, "glassfish-web.xml") || hasFile(files, "sun-web.xml")) {
            return "GlassFish";
        }
        if (files.stream().anyMatch(f -> "context.xml".equalsIgnoreCase(f.fileName()) && f.content().toLowerCase().contains("catalina"))) {
            return "Apache Tomcat";
        }
        if (hasFile(files, "web.xml")) {
            return "Servlet Container (unspecified)";
        }
        return "Unknown";
    }

    private boolean hasFile(List<ScannedFile> files, String fileName) {
        return files.stream().anyMatch(f -> f.fileName().equalsIgnoreCase(fileName));
    }

}
