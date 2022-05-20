<template>
  <div class="about">
    <div>
      <button @click="$router.push('budget')">Budget</button>
    </div>
    <br />
    <div>
      <button @click="$router.push('register')">Register</button>
    </div>
    <br />
    <div>
      <button @click="$router.push('shoppingList')">Shopping List</button>
    </div>
    <br />
    <div>
      <button @click="$router.push('notes')">Notes</button>
    </div>
  </div>
</template>
