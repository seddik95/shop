import { HttpClient } from "@angular/common/http";
import { Injectable, inject, signal } from "@angular/core";
import { Observable, switchMap, tap } from "rxjs";
import { Product } from "./product.model";

@Injectable({ providedIn: "root" })
export class ProductsService {
  private readonly http = inject(HttpClient);
  private readonly path = "/api/products";

  private readonly _products = signal<Product[]>([]);
  public readonly products = this._products.asReadonly();

  public get(): Observable<Product[]> {
    return this.http.get<Product[]>(this.path).pipe(tap((p) => this._products.set(p)));
  }

  public create(product: Product): Observable<Product[]> {
    return this.http.post<Product>(this.path, product).pipe(switchMap(() => this.get()));
  }

  public update(product: Product): Observable<Product[]> {
    return this.http.patch<Product>(`${this.path}/${product.id}`, product).pipe(switchMap(() => this.get()));
  }

  public delete(productId: number): Observable<Product[]> {
    return this.http.delete<void>(`${this.path}/${productId}`).pipe(switchMap(() => this.get()));
  }
}
