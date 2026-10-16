document.addEventListener("DOMContentLoaded", function () {
  const qty = document.getElementById("qtyInput");
  const max = qty ? parseInt(qty.getAttribute("max"), 10) : 0;
  document.querySelectorAll("[data-qty]").forEach(function (btn) {
    btn.addEventListener("click", function () {
      if (!qty) return;
      const delta = parseInt(btn.getAttribute("data-qty"), 10);
      const next = Math.min(max, Math.max(1, parseInt(qty.value || "1", 10) + delta));
      qty.value = next;
    });
  });
});
