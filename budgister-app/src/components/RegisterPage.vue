<template>
  <button @click="$router.push('edit')" class="btn btn-primary">Edit</button>
  <br />
  <br />
  <section class="container">
    <div class="grid-container">
      <div class="grid-item">Date</div>
      <div class="grid-item">Posted</div>
      <div class="grid-item">Description</div>
      <div class="grid-item">Amount</div>
      <div class="grid-item">Balance</div>
    </div>
    <router-view />
  </section>
</template>
