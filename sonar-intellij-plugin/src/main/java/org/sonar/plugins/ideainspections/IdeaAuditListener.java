package org.sonar.plugins.ideainspections;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.BatchExtension;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;

/**
 * @since 2.3
 */
public class IdeaAuditListener implements BatchExtension {

  private final SensorContext context;
  private final Project project;
  private final RuleFinder ruleFinder;
  private Resource currentResource = null;

  public IdeaAuditListener(SensorContext context, Project project, RuleFinder ruleFinder) {
    this.context = context;
    this.project = project;
    this.ruleFinder = ruleFinder;
  }
/*
  public void auditStarted(AuditEvent event) {

  }

  public void auditFinished(AuditEvent event) {

  }

  public void fileStarted(AuditEvent event) {

  }

  public void fileFinished(AuditEvent event) {
    currentResource = null;
  }

  public void addError(AuditEvent event) {
    String ruleKey = getRuleKey(event);
    if (ruleKey != null) {
      Rule rule = ruleFinder.findByKey(IdeaInspectionsConstants.REPOSITORY_KEY, ruleKey);
      if (rule != null) {
        initResource(event);
        Violation violation = Violation.create(rule, currentResource)
            .setLineId(getLineId(event))
            .setMessage(getMessage(event));
        context.saveViolation(violation);
      }
    }
  }

  private void initResource(AuditEvent event) {
    if (currentResource == null) {
      String absoluteFilename = event.getFileName();
      currentResource = JavaFile.fromAbsolutePath(absoluteFilename, project.getFileSystem().getSourceDirs(), false);
    }
  }

  private String getRuleKey(AuditEvent event) {
    String key = null;
    try {
      key = event.getModuleId();
    } catch (Exception e) {
      // checkstyle throws a NullPointer if the message is not set
    }
    if (StringUtils.isBlank(key)) {
      try {
        key = event.getSourceName();
      } catch (Exception e) {
        // checkstyle can throw a NullPointer if the message is not set
      }
    }
    return key;
  }

  private String getMessage(AuditEvent event) {
    try {
      return event.getMessage();

    } catch (Exception e) {
      // checkstyle can throw a NullPointer if the message is not set
      return null;
    }
  }

  private int getLineId(AuditEvent event) {
    try {
      return event.getLine();

    } catch (Exception e) {
      // checkstyle can throw a NullPointer if the message is not set
      return 0;
    }
  }

  public void addException(AuditEvent event, Throwable throwable) {
    // TODO waiting for sonar technical events ?
  }

  Resource getCurrentResource() {
    return currentResource;
  }
  */
}
