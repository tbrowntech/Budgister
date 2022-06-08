<template>
  <section>
    <h1>{{ header || "Welcome" }}</h1>
    <input v-model="newItem" type="text" placeholder="Add an item" />
    <label>
      <input type="checkbox" v-model="newItemHighPriority" /> High Priority
    </label>
    <br />
    {{ newItem }} {{ newItemHighPriority }}
    <ul>
      <li v-for="item in items" :key="item.id">{{ item.label }}</li>
    </ul>
  </section>
</template>
<script>
export default {
  data: () => ({
    header: "Shopping List App",
    newItem: "",
    newItemHighPriority: false,
    items: [
      { id: 1, label: "10 party hats" },
      { id: 2, label: "2 board games" },
      { id: 3, label: "20 cups" },
    ],
  }),
};
</script>
