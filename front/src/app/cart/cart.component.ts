import { CurrencyPipe } from "@angular/common";
import { Component, inject } from "@angular/core";
import { RouterLink } from "@angular/router";
import { ButtonModule } from "primeng/button";
import { CartService } from "./cart.service";

@Component({
  selector: "app-cart",
  standalone: true,
  styleUrls: ["./cart.component.scss"],
  imports: [ButtonModule, CurrencyPipe, RouterLink],
  template: `
    <section class="cart-page">
      <header class="page-heading">
        <span class="section-eyebrow">VOTRE SÉLECTION</span>
        <h1>Mon panier</h1>
        <p>Les essentiels que vous avez choisis.</p>
      </header>

      @if (cart.items().length === 0) {
        <div class="empty-state">
          <span class="empty-icon"><i class="pi pi-shopping-bag" aria-hidden="true"></i></span>
          <h2>Votre panier attend son premier coup de cœur</h2>
          <p>Parcourez la sélection et ajoutez-y vos produits préférés.</p>
          <a routerLink="/products/list">Découvrir les produits <i class="pi pi-arrow-right" aria-hidden="true"></i></a>
        </div>
      } @else {
        <div class="cart-layout">
          <div class="cart-items">
            @for (line of cart.items(); track line.productId) {
              <article class="cart-item">
                <span class="item-icon"><i class="pi pi-box" aria-hidden="true"></i></span>
                <div class="item-details">
                  <h2>{{ line.product.name }}</h2>
                  <p>{{ line.product.category }} <span>·</span> {{ line.product.price | currency: "EUR" }} / unité</p>
                </div>
                <div class="quantity-control">
                  <p-button icon="pi pi-minus" size="small" [rounded]="true" [outlined]="true" aria-label="Diminuer la quantité"
                    (onClick)="cart.setQuantity(line.productId, line.quantity - 1)" />
                  <span data-testid="qty">{{ line.quantity }}</span>
                  <p-button icon="pi pi-plus" size="small" [rounded]="true" [outlined]="true" aria-label="Augmenter la quantité"
                    (onClick)="cart.setQuantity(line.productId, line.quantity + 1)" />
                </div>
                <strong class="item-total">{{ line.quantity * line.product.price | currency: "EUR" }}</strong>
                <p-button icon="pi pi-trash" [text]="true" severity="danger" aria-label="Supprimer du panier"
                  (onClick)="cart.remove(line.productId)" />
              </article>
            }
          </div>
          <aside class="cart-summary">
            <h2>Récapitulatif</h2>
            <div><span>Articles</span><strong>{{ cart.count() }}</strong></div>
            <div class="summary-total"><span>Total</span><strong>{{ cart.total() | currency: "EUR" }}</strong></div>
            <a routerLink="/products/list">Continuer mes achats</a>
          </aside>
        </div>
      }
    </section>
  `,
})
export class CartComponent {
  public readonly cart = inject(CartService);
}
