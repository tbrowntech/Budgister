<template>
  <div class="home">
    <img alt="tbrowntech logo" src="../assets/logo.png" />
    <BottomNav msg="Welcome to Budgister" />
  </div>
</template>

<script>
// @ is an alias to /src
import BottomNav from "../components/BottomNav.vue";

export default {
  name: "bottomNav",
  components: {
    BottomNav,
  },
};
</script>
