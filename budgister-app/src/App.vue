<template>
  <section>
    <h1>Budigster</h1>
    <p class="center">The app to tell you where your money goes</p>
    <div class="top-nav">
      <button @click="$router.push('main')" class="btn btn-primary">
        Main
      </button>
    </div>
    <br />
    <router-view />
  </section>
</template>

<!--mongodb+srv://cluster0.avcgkkh.mongodb.net/?authSource=%24external&authMechanism=MONGODB-X509&retryWrites=true&w=majority-->
