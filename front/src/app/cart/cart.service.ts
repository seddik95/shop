import { HttpClient } from "@angular/common/http";
import { Injectable, computed, effect, inject, signal } from "@angular/core";
import { AuthService } from "app/auth/auth.service";
import { Product } from "app/products/data-access/product.model";

export interface CartLine {
  productId: number;
  quantity: number;
  product: Product;
}

@Injectable({ providedIn: "root" })
export class CartService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);
  private readonly path = "/api/cart";

  private readonly _items = signal<CartLine[]>([]);
  public readonly items = this._items.asReadonly();
  /** Badge value: recomputed automatically whenever the cart changes. */
  public readonly count = computed(() => this._items().reduce((n, l) => n + l.quantity, 0));
  public readonly total = computed(() => this._items().reduce((s, l) => s + l.quantity * l.product.price, 0));

  constructor() {
    effect(() => {
      if (this.auth.isLoggedIn()) this.load();
      else this._items.set([]);
    }, { allowSignalWrites: true });
  }

  quantityOf(productId: number): number {
    return this._items().find((l) => l.productId === productId)?.quantity ?? 0;
  }

  load() {
    this.http.get<CartLine[]>(this.path).subscribe((l) => this._items.set(l));
  }

  add(productId: number, quantity = 1) {
    this.http.post<CartLine>(this.path, { productId, quantity }).subscribe(() => this.load());
  }

  setQuantity(productId: number, quantity: number) {
    if (quantity < 1) return this.remove(productId);
    this.http.patch<CartLine>(`${this.path}/${productId}`, { quantity }).subscribe((line) =>
      this._items.update((items) => items.map((l) => (l.productId === productId ? line : l))),
    );
  }

  remove(productId: number) {
    this.http.delete(`${this.path}/${productId}`).subscribe(() =>
      this._items.update((items) => items.filter((l) => l.productId !== productId)),
    );
  }
}
