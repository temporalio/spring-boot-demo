var customerOnboardingApplication = {
    "id": "customeronboarding",
    "name": "Customer Onboarding Workflow",
    "version": "1.0",
    "specVersion": "0.7",
    "timeouts": {
        "workflowExecTimeout": {
            "duration": "PT1M"
        },
        "actionExecTimeout": "PT10S"
    },
    "retries": [
        {
            "name": "WorkflowRetries",
            "delay": "PT3S",
            "maxAttempts": 10
        }
    ],
    "start": "OnboardNewCustomer",
    "states": [
        {
            "name": "OnboardNewCustomer",
            "type": "switch",
            "dataConditions": [
                {
                    "condition": "$..[?(@.onto=='CLOUD')]",
                    "transition": "OnboardOntoCloud"
                },
                {
                    "condition": "$..[?(@.onto=='ZENDESK')]",
                    "transition": "OnboardOntoZendesk"
                },
                {
                    "condition": "$..[?(@.onto=='SLACK')]",
                    "transition": "OnboardOntoSlack"
                }
            ],
            "defaultCondition": {
                "transition": "OnboardOntoCloud"
            }
        },
        {
            "name": "OnboardOntoCloud",
            "type": "operation",
            "actions": [
                {
                    "name": "Invoke Onboard To Cloud Function",
                    "functionRef": "OnboardToCloud",
                    "sleep": {
                        "before": "PT1S"
                    }
                },
                {
                    "name": "Invoke Onboard To Zendesk Function",
                    "functionRef": "OnboardToZendesk",
                    "sleep": {
                        "before": "PT1S"
                    }
                },
                {
                    "name": "Invoke Onboard To Slack Function",
                    "functionRef": "OnboardToSlack",
                    "sleep": {
                        "before": "PT1S"
                    }
                }
            ],
            "end": true
        },
        {
            "name": "OnboardOntoZendesk",
            "type": "operation",
            "actions": [
                {
                    "name": "Invoke Onboard To Zendesk Function",
                    "functionRef": "OnboardToZendesk",
                    "sleep": {
                        "before": "PT1S"
                    }
                }
            ],
            "end": true
        },
        {
            "name": "OnboardOntoSlack",
            "type": "operation",
            "actions": [
                {
                    "name": "Invoke Onboard To Slack Function",
                    "functionRef": "OnboardToSlack",
                    "sleep": {
                        "before": "PT1S"
                    }
                }
            ],
            "end": true
        }
    ],
    "functions": [
        {
            "name": "OnboardToCloud",
            "type": "rest",
            "operation": "openapiactivities.json#onboardtocloud"
        },
        {
            "name": "OnboardToZendesk",
            "type": "rest",
            "operation": "openapiactivities.json#onboardtozendesk"
        },
        {
            "name": "OnboardToSlack",
            "type": "rest",
            "operation": "openapiactivities.json#onboardtoslack"
        }
    ]
};

var examplesMap = {};
examplesMap['customeronboarding'] = customerOnboardingApplication;

function selectExample(value) {
    if(value.length > 0) {
        var example = examplesMap[value];
        var model = monaco.editor.getModels()[0];
        model.setValue(JSON.stringify(example, null, 2));

        generateDiagram();
    }
}

function generateDiagram() {
    const { Specification, MermaidDiagram } = serverWorkflowSdk;

    const model = monaco.editor.getModels()[0];
    const modelVal = model.getValue();

    const mermaidSource = new MermaidDiagram(Specification.Workflow.fromSource(modelVal)).sourceCode();
    const mermaidDiv = document.querySelector(".workflowdiagram");

    mermaid.mermaidAPI.render('mermaid', mermaidSource, svgCode => {
        mermaidDiv.innerHTML = svgCode;
    });

}

function callUrl(type, url, data, callback){

    var xhr = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
    xhr.open('POST', url);
    xhr.onreadystatechange = function() {
        if (xhr.readyState>3 && xhr.status==200) { callback(xhr.responseText); }
    };
    xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.setRequestHeader('Accept', 'application/svg+xml');
    xhr.send(data);
    return xhr;
}

function changeTheme(theme) {
    if(theme.length > 0) {
        monaco.editor.setTheme(theme);
    }
}
