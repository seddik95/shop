import { HttpClient } from "@angular/common/http";
import { Injectable, computed, effect, inject, signal } from "@angular/core";
import { AuthService } from "app/auth/auth.service";
import { Product } from "app/products/data-access/product.model";

@Injectable({ providedIn: "root" })
export class WishlistService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);
  private readonly path = "/api/wishlist";

  private readonly _items = signal<Product[]>([]);
  public readonly items = this._items.asReadonly();
  public readonly count = computed(() => this._items().length);

  constructor() {
    effect(() => {
      if (this.auth.isLoggedIn()) this.load();
      else this._items.set([]);
    }, { allowSignalWrites: true });
  }

  has(productId: number): boolean {
    return this._items().some((p) => p.id === productId);
  }

  load() {
    this.http.get<Product[]>(this.path).subscribe((l) => this._items.set(l));
  }

  toggle(product: Product) {
    if (this.has(product.id)) {
      this.http.delete(`${this.path}/${product.id}`).subscribe(() =>
        this._items.update((l) => l.filter((p) => p.id !== product.id)));
    } else {
      this.http.post<Product>(`${this.path}/${product.id}`, {}).subscribe((p) =>
        this._items.update((l) => [...l, p]));
    }
  }
}