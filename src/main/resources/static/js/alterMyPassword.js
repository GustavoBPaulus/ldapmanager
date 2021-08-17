/**
 * 
 */
var host = document.location.host;




function resetPassword() {
	var alterMyPasswordModel = new Object();
	alterMyPasswordModel.user = $("#usernameModal").val();
	alterMyPasswordModel.typeOfUser = $("#selectTypeOfUserModal").val();
	

	var alterMyPasswordModelJson = JSON.stringify(alterMyPasswordModel);

	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: 'http://' + host + '/ldapprincipal/forgot-password',
		dataType: "json",
		data: alterMyPasswordModelJson,
		cache: false,
		async: false,
		success: function(response) {
			console.log(response);
			if (response) {
				alert('senha enviada para o seu e-mail');
			}
			else {
				alert('usuário não encontrado, ou emai não vinculado ao usuário, contate a equipe de TI');
			}


		},
		error: function(jqXHR, textStatus, errorThrown) {
			$('#divError').removeClass('messageOff');
			alert('erro ao resetar a senha');
			// ////alert('Erro criando contato: ' + jqXHR.responseText);
		}
	});
}



function changePassword() {
	var alterMyPasswordModel = new Object();
	alterMyPasswordModel.user = $("#user").val();
	alterMyPasswordModel.actualPassword = $('#inputPassword').val();
	alterMyPasswordModel.newPassword = $('#inputNewPassword').val();
	alterMyPasswordModel.typeOfUser = $('#selectTypeOfUser').val();
	
	var alterMyPasswordModelJson = JSON.stringify(alterMyPasswordModel);

	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: 'http://' + host + '/ldapprincipal/changepassword',
		dataType: "json",
		data: alterMyPasswordModelJson,
		cache: false,
		async: false,
		success: function(response) {
			console.log(response);
			if (response) {
				alert('senha alterada com sucesso');
			}
			else {
				alert('usuário ou senha incorreto');
			}


		},
		error: function(jqXHR, textStatus, errorThrown) {
			$('#divError').removeClass('messageOff');
			alert('erro ao alterar a senha');
			// ////alert('Erro criando contato: ' + jqXHR.responseText);
		}
	});
}