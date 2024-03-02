# ✨ Rushly
Elevating your shopping experience. Built on Kotlin and MVVM principles, enjoy a sleek interface, easy navigation, and seamless browsing experience for a stress-free shopping spree.

## Screenshots 📱
<div align="center">
<img src="https://github.com/Mahmud0808/Rushly/blob/master/screenshots/home.jpg" width="18%" /> <img src="https://github.com/Mahmud0808/Rushly/blob/master/screenshots/search.jpg" width="18%" /> <img src="https://github.com/Mahmud0808/Rushly/blob/master/screenshots/cart.jpg" width="18%" /> <img src="https://github.com/Mahmud0808/Rushly/blob/master/screenshots/profile.jpg" width="18%" /> <img src="https://github.com/Mahmud0808/Rushly/blob/master/screenshots/product.jpg" width="18%" />
<img src="https://github.com/Mahmud0808/Rushly/blob/master/screenshots/orders.jpg" width="18%" /> <img src="https://github.com/Mahmud0808/Rushly/blob/master/screenshots/status.jpg" width="18%" />
</div>

## Architecture 🗼

This app uses [***Firebase***](https://firebase.google.com/) services and Model-View-ViewModel (MVVM) architecture.

## Build-Tool 🧰

You need to have [Android Studio Hedgehog or above](https://developer.android.com/studio) to build this project.

## Getting Started 🚀

- In Android Studio project, go to `Tools` > `Firebase` > `Authentication` > `Authenticate using a custom authentication system`:
  - First, `Connect to Firebase`
  - After that, `Add the Firebase Authentication SDK to your app`

- Now open your project's [Firebase Console](https://console.firebase.google.com/) > `Authentication` > `Sign-in method`:
  - Enable `Email/Password`
  - Do not enable `Email link (passwordless sign-in)`

- Enable `Firestore Database`, open `Rules` tab and use this rule:

```
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {    
    // Everyone can read, but no one can write to admin collection
    match /admin/{adminId} {
      allow read: if request.auth != null;
			allow write: if false;
    }

    // Users can read user collection, and can edit their own collection
    match /user/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && (request.auth.uid == userId || isUserAdmin());
      
      match /cart/{document=**} {
        allow read, write: if request.auth != null && (request.auth.uid == userId || isUserAdmin());
      }
      
      match /address/{document=**} {
        allow read, write: if request.auth != null && (request.auth.uid == userId || isUserAdmin());
      }
      
      match /order/{document=**} {
        allow read, write: if request.auth != null && (request.auth.uid == userId || isUserAdmin());
      }
    }
    
    // Allow read access to product for everyone, but write access only for admins
    match /product/{productId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && isUserAdmin();
    }
    
    // Allow users to read order list if the order contains their userId
    // Or, allow read and write access to list of order only for admins
    match /order/{orderId} {
    	allow read: if request.auth != null && (resource.data.userId == request.auth.uid || isUserAdmin());
      allow write: if request.auth != null && isUserAdmin();
    }

    // Function to check if the user is an admin
    function isUserAdmin() {
      return get(/databases/$(database)/documents/admin/$(request.auth.uid)).data != null;
    }
  }
}
```

- Enable `Firebase Storage`

- That's it. Now you are good to go!

## Some info 📌

- Adding new products is a task reserved for admins only. If you want to make someone an admin, start by creating a user account within the app. Then, in your Firestore database, create a collection called `admin`. Find the document ID of the new user account you just made in the database. Now, create a new document within the admin collection and paste that ID as its document ID. And that’s all there is to it!

- To update the status of an order, like pending, confirmed, shipped, or delivered, follow these steps:

  - Navigate to the "All Orders" page.
  - Locate and select the specific order you wish to update.
  - Click on the step view to update the status accordingly.

  It's as simple as that!

## Contact 📩

Wanna reach out to me? DM me at 👇

Email: mahmudul15-13791@diu.edu.bd

## Credits 🤝

- [Make Your Own E-COMMERCE - Shopping APP Android](https://www.youtube.com/playlist?list=PLzZEuVaFb9ExqUwxMoXg0Li0wYW2IeAkz) by Landofcoding

## Disclaimer 📋

I decided to work on this project to learn Kotlin, Coroutines, and MVVM architecture. Just to be clear, I don't own any part of it. I simply fixed some problems and added features to improve it.
