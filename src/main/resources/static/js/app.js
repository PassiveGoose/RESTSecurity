$(document).ready(async function () {
    console.log("Document ready, fetching users...");
    await getTableWithUsers();
    await addNewUser();
});

const userFetchService = {
    head: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Referer': null
    },
    findAllUsers: async () => await fetch('/api/users', { method: 'GET' }),
    addNewUser: async (user) => await fetch('api/users', {method: 'POST', headers: userFetchService.head, body: JSON.stringify(user)})
}

async function getTableWithUsers() {
    let table = $('#usersTable tbody');
    table.empty();

    try {
        const response = await userFetchService.findAllUsers();
        const users = await response.json();
        console.log("Fetched users:", users); // Check fetched users in console

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
                    <button type="button" data-userid="${user.id}" data-action="edit" class="btn text-light btn-info"
                    data-bs-toggle="modal" data-bs-target="#editUserModal">Edit</button>
                </td>
                <td>
                    <button type="button" data-userid="${user.id}" data-action="delete" class="btn btn-danger"
                    data-bs-toggle="modal" data-bs-target="#deleteUserModal">Delete</button>
                </td>
            </tr>`;
            table.append(tableFilling);
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
        let userRole = addUserForm.find('#checkboxUser').val().trim();
        let adminRole = addUserForm.find('#checkboxAdmin').val().trim();

        let data = {
            name: name,
            surname: surname,
            age: age,
            username: username,
            password: password
        }
        const response = await userFetchService.addNewUser(data);
        if (response.ok) {
            await getTableWithUsers();
            addUserForm.find('#AddNewUserLogin').val('');
            addUserForm.find('#AddNewUserPassword').val('');
            addUserForm.find('#AddNewUserAge').val('');
        } else {
            let body = await response.json();
            let alert = `<div class="alert alert-danger alert-dismissible fade show col-12" role="alert" id="sharaBaraMessageError">
                            ${body.info}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            addUserForm.prepend(alert)
        }
    })
}