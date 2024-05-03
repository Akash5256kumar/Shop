package com.indevo.shop.viewmodel
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.indevo.shop.data.User
import com.indevo.shop.utils.Constants.USER_COLLECTION
import com.indevo.shop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

//import javax.inject.Inject

//@HiltViewModel
class RegisterViewModel (private val firebaseAuth:FirebaseAuth,private val db: FirebaseFirestore):ViewModel(){

private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> =_register
    fun createAccountWithEmailAndPassword(user:User,password:String){
        runBlocking {
        _register.emit(Resource.Loading())}

        firebaseAuth.createUserWithEmailAndPassword(user.email,password)
            .addOnSuccessListener {
                it.user?.let {
                    saveUserInfo(it.uid,user)

                }

            }
            .addOnFailureListener {
                _register.value=Resource.Error(it.message.toString())

            }
    }

    private fun saveUserInfo(userid:String,user: User) {
            db.collection(USER_COLLECTION)
                .document(userid)
                .set(user)
                .addOnSuccessListener {
                    _register.value=Resource.Sucess(user)
                }
                .addOnFailureListener {
                    _register.value=Resource.Error(it.message.toString())
                }


    }
}