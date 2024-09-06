$(document).ready(async function () {
    console.log("Document ready");
    await getTableWithUsers();
    await addNewUser();
});

function getCsrfToken() {
    return document.querySelector('meta[name="_csrf"]').getAttribute('content');
}

const userFetchService = {
    head: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'X-XSRF-TOKEN': getCsrfToken(),
        'Referer': null
    },
    findAllUsers: async () => await fetch('/api/users', { method: 'GET' }),
    findOneUser: async (id) => await fetch(`api/users?id=` + id, {method: 'GET'}),
    addNewUser: async (user, roles) => {
        const headers = userFetchService.head;

        const body = {
            user: user,
            roles: roles
        };

        return await fetch('/api/users', {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(body)
        });
    },
    deleteUser: async (id) =>  {
        const headers = userFetchService.head;
        await fetch('api/users', {
            method: 'DELETE',
            headers: headers,
            body: JSON.stringify(id)
        })
    },
    updateUser: async (id, user, roles) => {
        const headers = userFetchService.head;

        const body = {
            user: user,
            roles: roles
        };

        return await fetch('/api/users?id=' + id, {
            method: 'PUT',
            headers: headers,
            body: JSON.stringify(body)
        });
    }
}

async function getTableWithUsers() {
    let table = $('#usersTable tbody');
    table.empty();

    try {
        const response = await userFetchService.findAllUsers();
        const users = await response.json();
        console.log("Fetched users:", users);

        users.forEach(user => {
            let rolesString = user.roles.map(role => role.name).join(', ');
            let tableFilling = `<tr>
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.surname}</td>
                <td>${user.age}</td>
                <td>${user.username}</td>
                <td>${rolesString}</td>
                <td>
                    <button id="editButton" type="button" data-userid="${user.id}" data-action="edit" class="btn text-light btn-info"
                    data-bs-toggle="modal" data-bs-target="#editUserModal">Edit</button>
                </td>
                <td>
                    <button type="button" data-userid="${user.id}" data-action="delete" class="btn btn-danger"
                    data-bs-toggle="modal" data-bs-target="#deleteUserModal">Delete</button>
                </td>
            </tr>`;
            table.append(tableFilling);

            document.getElementById('editUserModal').addEventListener('show.bs.modal', function (event) {
                let userId = event.relatedTarget.getAttribute('data-userid');
                let editUserModal = $('#editUserModal');
                editUser(editUserModal, userId);
            });
            document.getElementById('deleteUserModal').addEventListener('show.bs.modal', function (event) {
                let userId = event.relatedTarget.getAttribute('data-userid');
                let deleteUserModal = $('#deleteUserModal');
                deleteUser(deleteUserModal, userId);
            })
        });
    } catch (error) {
        console.error('Error filling table with users:', error);
    }
}

async function addNewUser() {
    $('#addNewUserButton').click(async () =>  {
        let addUserForm = $('#addUserForm')
        let name = addUserForm.find('#name').val().trim();
        let surname = addUserForm.find('#surname').val().trim();
        let age = addUserForm.find('#age').val().trim();
        let username = addUserForm.find('#username').val().trim();
        let password = addUserForm.find('#password').val().trim();

        let roles = [];
        if (addUserForm.find('#checkboxUser').is(':checked')) {
            roles.push('USER');
        }
        if (addUserForm.find('#checkboxAdmin').is(':checked')) {
            roles.push('ADMIN');
        }
        let data = {
            name: name,
            surname: surname,
            age: age,
            username: username,
            password: password
        }
        const response = await userFetchService.addNewUser(data, roles);
        if (response.ok) {
            await getTableWithUsers();
            addUserForm.find('#name').val('');
            addUserForm.find('#surname').val('');
            addUserForm.find('#age').val('');
            addUserForm.find('#username').val('');
            addUserForm.find('#password').val('');
            addUserForm.find('#checkboxUser').val(false);
            addUserForm.find('#checkboxAdmin').val(false);
        } else {
            console.error('smth wrong');
            let alert = `<div class="alert alert-danger alert-dismissible fade show col-12" role="alert" id="sharaBaraMessageError">                         
                            <button type="button" class="btn close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            addUserForm.prepend(alert)
        }
    })
}

async function editUser(modal, id) {
    let preUser = await userFetchService.findOneUser(id);
    let user = preUser.json();

    modal.find('.modal-footer').empty();
    let editButton = '<button type="submit" class="btn btn-primary" id="editButton">Edit</button>';
    let closeButton = '<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>';
    modal.find('.modal-footer').append(editButton);
    modal.find('.modal-footer').append(closeButton);

    user.then(user => {
        modal.find('.modal-title').html('Edit user - ' + user.username);
        modal.find('#editName').prop('value', user.name);
        modal.find('#editSurname').prop('value', user.surname);
        modal.find('#editAge').prop('value', user.age);
        modal.find('#editUsername').prop('value', user.username);
        for (let i = 0; i < user.roles.length; i++) {
            if (user.roles[i].name === 'USER') {
                modal.find('#editRoleUser').prop('checked', true);
            }
        }
        for (let i = 0; i < user.roles.length; i++) {
            if (user.roles[i].name === 'ADMIN') {
                modal.find('#editRoleAdmin').prop('checked', true);
            }
        }
    })

    modal.find('#editButton').on('click', async () => {
        let name = modal.find('#editName').val();
        let surname = modal.find('#editSurname').val();
        let age = modal.find("#editAge").val();
        let username = modal.find('#editUsername').val();
        let data = {
            name: name,
            surname: surname,
            age: age,
            username: username
        }
        let roles = [];
        if (modal.find('#editRoleUser').is(':checked')) {
            roles.push('USER');
        }
        if (modal.find('#editRoleAdmin').is(':checked')) {
            roles.push('ADMIN');
        }
        const response = await userFetchService.updateUser(id, data, roles);

        if (response.ok) {
            await getTableWithUsers();
            modal.modal('hide');
        } else {
            let alert = `<div class="alert alert-danger alert-dismissible fade show col-12" role="alert" id="sharaBaraMessageError">                         
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            modal.find('.modal-body').prepend(alert);
        }
    })
}

async function deleteUser(modal, id) {
    let preUser = await userFetchService.findOneUser(id);
    let user = preUser.json();

    modal.find('.modal-footer').empty();
    let deleteButton = '<button type="submit" class="btn btn-danger" id="deleteButton">Delete</button>';
    let closeButton = '<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>';
    modal.find('.modal-footer').append(deleteButton);
    modal.find('.modal-footer').append(closeButton);

    user.then(user => {
        modal.find('.modal-title').html('Delete user - ' + user.username);
        modal.find('#deleteName').prop('value', user.name);
        modal.find('#deleteSurname').prop('value', user.surname);
        modal.find('#deleteAge').prop('value', user.age);
        modal.find('#deleteUsername').prop('value', user.username);
        for (let i = 0; i < user.roles.length; i++) {
            if (user.roles[i].name === 'USER') {
                modal.find('#deleteRoleUser').prop('checked', true);
            }
        }
        for (let i = 0; i < user.roles.length; i++) {
            if (user.roles[i].name === 'ADMIN') {
                modal.find('#deleteRoleAdmin').prop('checked', true);
            }
        }
    })

    modal.find('#deleteButton').on('click', async () => {
        await userFetchService.deleteUser(id);
        await getTableWithUsers();
        modal.modal('hide');
    })
}