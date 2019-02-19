import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
cred = credentials.Certificate('darkopoppin.json')
default_app = firebase_admin.initialize_app(cred)


db = firestore.client()
doc_ref = db.collection(u'users').document(u'sabahov')
i = 0
while True:
    doc_ref.set({
        u'first': u'darko',
        u'middle': u'some',
        u'last': u'sabahov',
        u'born': 1998 + i
    })
    i += 1

  

