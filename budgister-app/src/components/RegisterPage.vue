<template>
<<<<<<< HEAD
  <section>
    <button id="edit">Edit</button>
    <br />
    <br />
    <div class="grid-container" id="grid-title">
      <div>Date</div>
      <div>Posted</div>
      <div>Description</div>
      <div>Amount</div>
      <div>Balance</div>
    </div>
    <div class="grid-container">
      <div>6/13/22</div>
      <div>Yes</div>
      <div>Walmart</div>
      <div>$52.00</div>
      <div>$1592.00</div>
=======
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
>>>>>>> 6aaa157dc96cd1f83a65e71ab7a39f4e25ea5f9a
    </div>
    <router-view />
  </section>
</template>
