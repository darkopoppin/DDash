import pyrebase
import time
"""
Database user program which checks if the user already has access to the
database. If not, asks the user to create one.
"""
"""
User already has an account/chose option "1" and wishes to sign in.
Asks the user to enter credentials to sign in to the database
"""
def login():
    print("To sign in you must enter the following:\n")
    email = input("Email- ")
    password = input("Password- ")
    # try:
    user = auth.sign_in_with_email_and_password(email,password)
    return(user)
    # except Exception as e:
    # print("Something went wrong!")
    # print(auth.get_account_info(user['idToken']))

"""
User does not have a registered email with database/chose option 2.
Asks the user to enter an email address to register.
"""
def sign_up():
    email = input("Enter email you wish to register:\n")
    password = input("Enter your password\n")
    user = auth.create_user_with_email_and_password(email, password)
    print("Please check your email to finish registering")
    auth.send_email_verification(user['idToken'])


"""
Database configurations
**Confindential - top secret config which has the key tp meg's heart**
"""
config = {
    "apiKey": "AIzaSyAoMamWUDMpygGjdVCO3lCsWFA1aeTjYh4",
    "authDomain": "darkopoppin.firebaseapp.com",
    "databaseURL": "https://darkopoppin.firebaseio.com",
    "projectId": "darkopoppin",
    "storageBucket": "darkopoppin.appspot.com",
    "messagingSenderId": "790314940142"
    }

"""
Initialize firebase using the pyrebase API with the config which we have given.
"""
firebase = pyrebase.initialize_app(config)


"""
Reference to the authentication service
"""
auth = firebase.auth()


"""
Reference to the database service
"""
db = firebase.database()

def gatekeeping():
    request = input("Please Enter 1 to log in ||| 2 to sign up\n")
    if request == "1":
        return(login())
    elif request == "2":
        sign_up()
        gatekeeping()
    else:
        # print("Please enter a valid option")
        gatekeeping()
    print("The process of login/sign up has finished")


def main():
    user = gatekeeping()
    print("Displaying user idToken")
    time.sleep(3)
    print(auth.get_account_info(user['idToken']))
    print("This program is closing in 5 seconds")
    time.sleep(5)
if __name__ == '__main__':
    main()