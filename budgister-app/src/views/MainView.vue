<template>
  <div class="about">
    <div>
      <button @click="$router.push('budget')" class="btn btn-primary">
        Budget
      </button>
    </div>
    <br />
    <div>
      <button @click="$router.push('register')" class="btn btn-primary">
        Register
      </button>
    </div>
    <br />
    <div>
      <button @click="$router.push('shoppinglist')" class="btn btn-primary">
        Shopping List
      </button>
    </div>
    <br />
    <div>
      <button @click="$router.push('notes')" class="btn btn-primary">
        Notes
      </button>
    </div>
  </div>
</template>
