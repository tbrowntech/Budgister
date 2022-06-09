<template>
  <section>
    <h1>{{ header || "Welcome" }}</h1>
    <div class="add-item-form">
      <input
        @keyup.enter="saveItem"
        type="text"
        v-model="newItem"
        placeholder="Add an item"
      />
      <label>
        <input type="checkbox" v-model="newItemHighPriority" />
        High Priority
      </label>
    </div>
    <br />
    <button @click="saveItem" class="btn btn-primary">Save Item</button>
    <br />
    <ul>
      <li v-for="item in items" :key="item.id">{{ item.label }}</li>
    </ul>
  </section>
</template>
<script>
export default {
  data: () => ({
    header: "Shopping List",
    newItem: "",
    newItemHighPriority: false,
    items: [
      { id: 1, label: "10 party hats" },
      { id: 2, label: "2 board games" },
      { id: 3, label: "20 cups" },
    ],
  }),
  methods: {
    saveItem() {
      this.items.push({ id: this.items.length + 1, label: this.newItem });
      this.newItem = "";
    },
  },
};
</script>
