const remoteUrl = "#remoteurl";
const remoteUsername = "#remoteusername";
const selectRepositoryTypeId = "#select-repository-type";

$(document).ready(function() {
    $(remoteUrl).hide();
    $(remoteUsername).hide();
    $("#organization").hide();
});

$(selectRepositoryTypeId).change(function() {
    var selectedRepository = $(selectRepositoryTypeId).val();
    if (selectedRepository != '') {
    var repositoryConfig = JSON.parse(selectedRepository);
        $("#plataform").val(repositoryConfig.plataform);
        if (repositoryConfig.askUrl == 'true') {
            $(remoteUrl).val('');
            $(remoteUrl).show(200);
            $(remoteUrl).prop( "readonly", false );
        } else {
            $(remoteUrl).val(repositoryConfig.url);
            $(remoteUrl).hide(200);
            $(remoteUrl).prop( "readonly", true );
        }
        if (repositoryConfig.askOrganization == 'true') {
            $("#organization").val('');
            $("#organization").show(200);
            $("#organization").prop( "readonly", false );
        } else {
            $("#organization").val('');
            $("#organization").hide(200);
            $("#organization").prop( "readonly", true );
        }
        if (repositoryConfig.askUsername == 'true') {
            $(remoteUsername).val('');
            $(remoteUsername).show(200);
            $(remoteUsername).prop( "readonly", false );
        } else {
            $(remoteUsername).val('');
            $(remoteUsername).hide(200);
            $(remoteUsername).prop( "readonly", true );
        }
    }
});

$("#login-form").submit(function() {
    var username = $(remoteUsername).val();
    var domain = $(remoteUrl).val();
    var plataform = $("#plataform").val();
    var organization = $("#organization").val();
    $("#principal").val('{"username":"'+ username +'","organization":"' + organization + '", "domain":"'+ domain +'", "plataform":"'+ plataform +'"}');
});
