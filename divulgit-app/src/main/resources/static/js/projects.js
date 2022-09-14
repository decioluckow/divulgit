function scanRemoteForProjets() {
    $.ajax({
        url:'/in/rest/remote/scan',
        method:'post',
        dataType:'json',
        success:function(data, textStatus, xhr){
            if (xhr.status == 200) {
               $("#projectsInProgressAlert").show(200);
               window.setTimeout('$("#projectsInProgressAlert").hide(200)', 5000);
            } else {
                alert('some error occurred try again');
            }
        },
        error:function(jqXHR, textStatus, errorThrown){
            logErrors(jqXHR, textStatus, errorThrown);
        }
    });
}

function hideProjectScanningAlertAndRefresh() {
   $("#projectsInProgressAlert").hide(200);
   window.location.reload();
}

function explore(projectId){
       window.location='/in/project/' + projectId + '/mergeRequests';
}

function activateProject(projectElement, projectId){
    $.ajax({
        url:'/in/project/'+ projectId +'/activate',
        method:'post',
        success:function(data, textStatus, jqXHR){
            console.log(data);
            if (jqXHR.status == 200) {
                $(projectElement).hide(250);
            } else {
                alert('some error occurred try again');
            }
        },
        error:function(jqXHR, textStatus, errorThrown){
            logErrors(jqXHR, textStatus, errorThrown);
        }
    });
}

function ignoreProject(projectElement, projectId){
    $.ajax({
        url:'/in/project/'+ projectId +'/ignore',
        method:'post',
        success:function(data, textStatus, jqXHR){
            console.log(data);
            if (jqXHR.status == 200) {
                $(projectElement).hide(250);
            } else {
                alert('some error occurred try again');
            }
        },
        error:function(jqXHR, textStatus, errorThrown){
            logErrors(jqXHR, textStatus, errorThrown);
        }
    });
}

function fillCurrentProjectId(projectId) {
    $("#currentProjectId").val(projectId);
}

function openStartScanModal(fromComponent) {
   fillCurrentProjectId(getProjectId(fromComponent));
   $("#startScanNumberInput").val(0);
}

function startScanFromFirst() {
   startScan(1);
}

function startScanFromNumber() {
   const scanFrom = $("#startScanNumberInput").val();
   startScan(scanFrom);
}

function startScan(scanFrom) {
    const projectId = $("#currentProjectId").val();
    if (!scanFrom) {
        alert('Please, fill the initial merge request number to consider in scanning. This is the mergerequest that your team started hashtagging')
    } else {
        $.ajax({
            url:'/in/project/' + projectId + '/scanFrom/' + scanFrom + '/',
            method:'post',
            dataType:'json',
            success:function(data, textStatus, xhr){
                if (xhr.status == 200) {
                    $("#startScanModal").modal("hide");
                    if ($("#redirectTasksPage").is(":checked")) {
                       window.location='/in/tasks';
                    }
                }
            },
            error:function(jqXHR, textStatus, errorThrown){
                logErrors(jqXHR, textStatus, errorThrown);
            }
        });
    }
}

function scanLastest(projectId){
    $.ajax({
        url:'/in/project/' + projectId + '/scanFrom/lastest/',
        method:'post',
        success:function(data, textStatus, xhr){
            if (xhr.status == 200) {
                $("#actions-"+projectId).html('go to <a href=\'/in/tasks\'>tasks</a> page');
            }
        },
        error:function(jqXHR, textStatus, errorThrown){
            logErrors(jqXHR, textStatus, errorThrown);
        }
    });
}

function getProjectId(element) {
    return getProjectElement(element).attr("data-project-id");
}

function getProjectElement(actualElement) {
    return $(actualElement).parents(".project-item")
}

function logErrors(jqXHR, textStatus, errorThrown) {
   console.log(jqXHR);
   console.log(textStatus);
   console.log(errorThrown);
   alert('Server error occured. Please check browser console.')
}
