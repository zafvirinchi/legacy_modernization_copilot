package com.ailegacy.modernization.copilot.infrastructure.analysis;

import com.ailegacy.modernization.copilot.domain.enums.TechnologyType;
import com.ailegacy.modernization.copilot.infrastructure.analysis.model.ScannedFile;
import com.ailegacy.modernization.copilot.infrastructure.analysis.model.TechnologyRule;
import com.ailegacy.modernization.copilot.infrastructure.analysis.model.TechnologySignal;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.ailegacy.modernization.copilot.domain.enums.TechnologyType.*;

/**
 * Declarative catalog of detection rules: one {@link TechnologyRule} per legacy
 * technology, each made up of independent signals. A signal matching in any file
 * counts as one distinct piece of evidence; see {@link ConfidenceScorer} for how
 * that turns into a score.
 */
@Component
public class TechnologyRuleCatalog {

    public List<TechnologyRule> rules() {
        return List.of(
                new TechnologyRule(SERVLET, List.of(
                        signal("Class extends HttpServlet", contentContains("extends HttpServlet")),
                        signal("Servlet API import", contentContains("javax.servlet.http", "jakarta.servlet.http")),
                        signal("@WebServlet annotation", contentContains("@WebServlet")),
                        signal("web.xml deployment descriptor", fileNameEquals("web.xml")),
                        signal("<servlet> declaration in web.xml", fileNameEquals("web.xml").and(contentContains("<servlet>")))
                )),
                new TechnologyRule(JSP, List.of(
                        signal("JSP file present", extensionEquals("jsp")),
                        signal("JSP scriptlet syntax", extensionEquals("jsp").and(contentContains("<%"))),
                        signal("JSP taglib directive", contentContains("<%@ taglib", "<jsp:"))
                )),
                new TechnologyRule(SPRING_MVC, List.of(
                        signal("@Controller / @RestController annotation", contentContains("@Controller", "@RestController")),
                        signal("Spring web bind annotations import", contentContains("org.springframework.web.bind.annotation")),
                        signal("Spring mapping annotation", contentContains("@RequestMapping", "@GetMapping", "@PostMapping", "@PutMapping", "@DeleteMapping")),
                        signal("Spring Boot application properties", contentContains("spring.mvc.", "spring.application.name"))
                )),
                new TechnologyRule(SPRING_XML, List.of(
                        signal("Spring beans XML namespace", contentContains("springframework.org/schema/beans")),
                        signal("<beans> root element", contentContains("<beans")),
                        signal("Spring application context file naming", fileNameMatches("applicationContext.*\\.xml", "spring-.*\\.xml", ".*-context\\.xml"))
                )),
                new TechnologyRule(JDBC, List.of(
                        signal("java.sql / javax.sql import", contentContains("java.sql.Connection", "java.sql.DriverManager", "javax.sql.DataSource")),
                        signal("JDBC connection string", contentContains("jdbc:")),
                        signal("Datasource URL property", contentContains("datasource.url", "spring.datasource"))
                )),
                new TechnologyRule(HIBERNATE, List.of(
                        signal("Hibernate package usage", contentContains("org.hibernate")),
                        signal("JPA @Entity annotation", contentContains("@Entity")),
                        signal("hibernate.cfg.xml configuration file", fileNameEquals("hibernate.cfg.xml")),
                        signal("Hibernate mapping XML", contentContains("hibernate-configuration", "hibernate-mapping")),
                        signal("Hibernate properties", contentContains("hibernate."))
                )),
                new TechnologyRule(EJB, List.of(
                        signal("EJB API import", contentContains("javax.ejb", "jakarta.ejb")),
                        signal("EJB bean annotation", contentContains("@Stateless", "@Stateful", "@MessageDriven", "@EJB")),
                        signal("ejb-jar.xml descriptor", fileNameEquals("ejb-jar.xml").or(contentContains("<ejb-jar")))
                )),
                new TechnologyRule(COBOL, List.of(
                        signal("COBOL source file present", extensionEquals("cbl")),
                        signal("IDENTIFICATION DIVISION", contentContainsIgnoreCase("IDENTIFICATION DIVISION")),
                        signal("PROCEDURE DIVISION", contentContainsIgnoreCase("PROCEDURE DIVISION"))
                )),
                new TechnologyRule(JCL, List.of(
                        signal("JCL source file present", extensionEquals("jcl")),
                        signal("JOB card", contentMatches(Pattern.compile("^//\\S+\\s+JOB\\b", Pattern.MULTILINE))),
                        signal("EXEC PGM statement", contentContains("EXEC PGM="))
                )),
                new TechnologyRule(STRUTS, List.of(
                        signal("Struts package usage", contentContains("org.apache.struts", "com.opensymphony.xwork2")),
                        signal("Struts Action subclass", contentContains("extends Action", "extends ActionSupport")),
                        signal("struts-config.xml / struts.xml descriptor", fileNameMatches("struts-config\\.xml", "struts\\.xml")),
                        signal("<struts-config> or <struts> root element", contentContains("<struts-config", "<struts>"))
                ))
        );
    }

    private static TechnologySignal signal(String description, Predicate<ScannedFile> matcher) {
        return new TechnologySignal(description, matcher);
    }

    private static Predicate<ScannedFile> contentContains(String... needles) {
        return file -> {
            for (String needle : needles) {
                if (file.content().contains(needle)) {
                    return true;
                }
            }
            return false;
        };
    }

    private static Predicate<ScannedFile> contentContainsIgnoreCase(String needle) {
        return file -> file.content().toUpperCase().contains(needle.toUpperCase());
    }

    private static Predicate<ScannedFile> contentMatches(Pattern pattern) {
        return file -> pattern.matcher(file.content()).find();
    }

    private static Predicate<ScannedFile> extensionEquals(String extension) {
        return file -> file.extension().equalsIgnoreCase(extension);
    }

    private static Predicate<ScannedFile> fileNameEquals(String fileName) {
        return file -> file.fileName().equalsIgnoreCase(fileName);
    }

    private static Predicate<ScannedFile> fileNameMatches(String... patterns) {
        return file -> {
            for (String pattern : patterns) {
                if (file.fileName().matches("(?i)" + pattern)) {
                    return true;
                }
            }
            return false;
        };
    }

}
