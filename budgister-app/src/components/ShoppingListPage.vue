<template>
  <section>
    <h3>Shopping List</h3>
    <div class="container">
      <ul>
        <li v-for="item in items" :key="item.id">{{ item.label }}</li>
      </ul>
    </div>
  </section>
</template>
<script>
export default {
  data: () => ({
    header: "Shopping List App",
    items: [
      { id: 1, label: "10 party hats" },
      { id: 2, label: "2 board games" },
      { id: 3, label: "20 cups" },
    ],
  }),
};
</script>
