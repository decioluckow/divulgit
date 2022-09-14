$(document).ready(function() {
  $('.comment-line').each(function(button) {
    toogleDiscussedButton(this);
  });
});

function toogleDiscussedButton(commentLine) {
  console.log('toogleDiscussedButton(commentLine)');
  var discussed = $(commentLine).data('discussed');
  console.log(discussed);
  var discussedButton = $(commentLine).find('.btn-mark-discussed');
  console.log(discussedButton);
  var undiscussedButton = $(commentLine).find('.btn-unmark-discussed');
  console.log(undiscussedButton);
  if (discussed) {
    $(discussedButton).hide();
    $(undiscussedButton).show();
  } else {
    $(discussedButton).show();
    $(undiscussedButton).hide();
  }
}

function markAsDiscussed(button, markDiscussed) {
  var commentLine = getParentByClass(button, '.comment-line');
  var mergeRequestId = commentLine.data('mergerequest-id');
  var commentId = commentLine.data('comment-id');
  $.ajax({
    url:'/in/mergeRequest/' + mergeRequestId + '/comment/' + commentId + '/discussed/' + markDiscussed,
    method:'post',
    success:function(data, textStatus, jqXHR){
      console.log(data);
      if (jqXHR.status == 200) {
        console.log('discusssed makr ' + markDiscussed);
        console.log('1toogleDiscussedButton(commentLine) ' + commentLine.data('discussed'));
        $(commentLine).data('discussed', markDiscussed);
        console.log('2toogleDiscussedButton(commentLine) ' + commentLine.data('discussed'));
        toogleDiscussedButton(commentLine);
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

function deleteComment(button) {
  var commentLine = getParentByClass(button, '.comment-line');
  var mergeRequestId = commentLine.data('mergerequest-id');
  var commentId = commentLine.data('comment-id');
  $.ajax({
    url:'/in/mergeRequest/' + mergeRequestId + '/comment/' + commentId + '/delete/',
    method:'post',
    success:function(data, textStatus, jqXHR){
      console.log(data);
      if (jqXHR.status == 200) {
        removeFromView(commentLine);
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

function hideComment(button) {
  var commentLine = getParentByClass(button, '.comment-line');
  var mergeRequestId = commentLine.data('mergerequest-id');
  var commentId = commentLine.data('comment-id');
  $.ajax({
    url:'/in/mergeRequest/' + mergeRequestId + '/comment/' + commentId + '/hide/',
    method:'post',
    success:function(data, textStatus, jqXHR){
      console.log(data);
      if (jqXHR.status == 200) {
        removeFromView(commentLine);
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

function removeFromView(commentLine) {
    var mergeRequestId = $(commentLine).data('mergerequest-id');
    $(commentLine).remove();
    var mergeRequestCommentsLength = document.querySelectorAll("tr[data-mergerequest-id='" + mergeRequestId + "'].comment-line").length;
    if (mergeRequestCommentsLength == 0) {
        var mergeRequestLine = document.querySelector("tr[data-mergerequest-id='" + mergeRequestId + "'].mergerequest-line");
        if (mergeRequestLine) {
            $(mergeRequestLine).hide(100);
        }
    }
}

function rescan(button) {
    var mergeRequestLine = getParentByClass(button, '.mergerequest-line');
    var mergeRequestId = mergeRequestLine.data('mergerequest-id');
    var projectId = mergeRequestLine.data('project-id');
    $.ajax({
        url: '/in/project/' + projectId + '/mergeRequest/' + mergeRequestId + '/rescan',
        method: 'post',
        success: function (response) {
            console.log(response)
            new bootstrap.Toast(document.getElementById('rescan-mergerequest-toast')).show();
            $(button).hide(2000);
        },
        error: function (response, errorThrown) {
            if (response.status >= 400 && response.status < 600) {
                console.log(errorThrown);
                alert('server error occured')
            }
        }
    });
}

function getParentByClass(element, parentClass) {
  return $(element).parents(parentClass);
}
