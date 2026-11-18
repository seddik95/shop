import { CurrencyPipe } from "@angular/common";
import { Component, inject } from "@angular/core";
import { RouterLink } from "@angular/router";
import { CartService } from "app/cart/cart.service";
import { ButtonModule } from "primeng/button";
import { WishlistService } from "./wishlist.service";

@Component({
  selector: "app-wishlist",
  standalone: true,
  styleUrls: ["./wishlist.component.scss"],
  imports: [ButtonModule, CurrencyPipe, RouterLink],
  template: `
    <section class="wishlist-page">
      <header class="page-heading">
        <span class="section-eyebrow">VOS INSPIRATIONS</span>
        <h1>Ma liste d’envies</h1>
        <p>Gardez vos pièces favorites tout près.</p>
      </header>

      @if (wishlist.items().length === 0) {
        <div class="empty-state">
          <span class="empty-icon"><i class="pi pi-heart" aria-hidden="true"></i></span>
          <h2>Votre liste est encore vide</h2>
          <p>Ajoutez vos coups de cœur ici pour les retrouver facilement.</p>
          <a routerLink="/products/list">Explorer la collection <i class="pi pi-arrow-right" aria-hidden="true"></i></a>
        </div>
      } @else {
        <div class="wishlist-grid">
          @for (p of wishlist.items(); track p.id) {
            <article class="wishlist-card">
              <div class="wishlist-art"><span>{{ p.name.charAt(0) }}</span></div>
              <div class="wishlist-details">
                <span class="product-category">{{ p.category }}</span>
                <h2>{{ p.name }}</h2>
                <strong>{{ p.price | currency: "EUR" }}</strong>
                <div class="wishlist-actions">
                  <p-button label="Ajouter au panier" icon="pi pi-shopping-bag"
                    [disabled]="p.inventoryStatus === 'OUTOFSTOCK'" (onClick)="cart.add(p.id)" styleClass="add-button" />
                  <p-button icon="pi pi-trash" [text]="true" severity="danger" aria-label="Retirer de la liste d’envies"
                    (onClick)="wishlist.toggle(p)" />
                </div>
              </div>
            </article>
          }
        </div>
      }
    </section>
  `,
})
export class WishlistComponent {
  public readonly wishlist = inject(WishlistService);
  public readonly cart = inject(CartService);
}