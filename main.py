from flask import Flask, request

app = Flask(__name__)


passwords = {
	'team1': {
		'simple': 'password1',
		'complex': 's0m3tHingC0mp13x!',
	},
	'team2': {
		'simple': 'password2',
		'complex': 's0m3tHingC0mp13x!',
	},
	'team3': {
		'simple': 'password3',
		'complex': 's0m3tHingC0mp13x!',
	},
}


@app.route('/login-simple', methods=['POST'])
def login_simple() -> flask.Response:
	return login(request.get_json(), 'simple')


@app.route('/login-complex', methods=['POST'])
def login_complex() -> flask.Response:
	return login(request.get_json(), 'complex')


def login(params: Dict[str, str], password_type: str) -> flask.Response:
	if 'username' not in params:
		return '`username` must be set in request', 400
	if 'password' not in params:
		return '`password` must be set in request', 400
	username = params['username']
	password = params['password']
	if username not in passwords:
		return f'Invalid username {username}', 200
	if password == passwords[username][password_type]:
		return f'...', 200
	return f'Incorrect password for username {username}.', 200


if __name__ == '__main__':
	app.run(host='0.0.0.0', port=8080)
