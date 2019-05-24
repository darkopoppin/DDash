from firebase_admin import auth
initializze_app()
user = auth.get_user_by_email('calidar9806@gmail.com')
print('Successfully fetched user data: {0}'.format(user.uid))
