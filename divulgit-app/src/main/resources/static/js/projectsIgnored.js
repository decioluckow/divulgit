function unIgnoreProject(projectElement, projectId){
    $.ajax({
        url:'/in/project/'+ projectId +'/unignore',
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
            console.log(jqXHR);
            console.log(textStatus);
            console.log(errorThrown);
            alert('server error occured')
        }
    });
}

function getProjectId(element) {
    return getProjectElement(element).attr("data-project-id");
}

function getProjectElement(actualElement) {
    return $(actualElement).parents(".project-item")
}
