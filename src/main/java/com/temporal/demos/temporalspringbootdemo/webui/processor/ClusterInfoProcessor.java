package com.temporal.demos.temporalspringbootdemo.webui.processor;

import io.temporal.client.WorkflowClient;
import com.temporal.demos.temporalspringbootdemo.webui.util.DialectUtils;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

public class ClusterInfoProcessor extends AbstractElementTagProcessor {
    private static final String TAG_NAME = "clusterinfo";
    private static final String DEFAULT_FRAGMENT_NAME = "~{temporalwebdialect :: clusterinfo}";
    private static final int PRECEDENCE = 10000;

    private final ApplicationContext ctx;
    private WorkflowClient workflowClient;

    public ClusterInfoProcessor(final String dialectPrefix, ApplicationContext ctx,
                                WorkflowClient workflowClient) {

        super(TemplateMode.HTML, dialectPrefix, TAG_NAME, true, null, false, PRECEDENCE);

        this.ctx = ctx;
        this.workflowClient = workflowClient;
    }

    @Override
    protected void doProcess(ITemplateContext templateContext, IProcessableElementTag processInstancesTag,
                             IElementTagStructureHandler structureHandler) {

        final IEngineConfiguration configuration = templateContext.getConfiguration();
        IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
        DialectUtils dialectUtils = new DialectUtils(workflowClient);

        structureHandler.setLocalVariable("clusterinfo", dialectUtils.getClusterInfo());

        final IModelFactory modelFactory = templateContext.getModelFactory();
        final IModel model = modelFactory.createModel();

        model.add(modelFactory.createOpenElementTag("div", "th:replace", dialectUtils.getFragmentName(
                processInstancesTag.getAttributeValue("fragment"), DEFAULT_FRAGMENT_NAME, parser, templateContext)));
        model.add(modelFactory.createCloseElementTag("div"));
        structureHandler.replaceWith(model, true);
    }
}
