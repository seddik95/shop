import { Component } from "@angular/core";
import { RouterLink, RouterLinkActive } from "@angular/router";

@Component({
  selector: "app-panel-menu",
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="side-nav" aria-label="Navigation principale">
      <span class="nav-caption">BOUTIQUE</span>
      <a routerLink="/home" routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }">
        <i class="pi pi-home" aria-hidden="true"></i><span>Accueil</span>
      </a>
      <a routerLink="/products/list" routerLinkActive="active">
        <i class="pi pi-th-large" aria-hidden="true"></i><span>Produits</span>
      </a>
      <span class="nav-caption nav-caption-spaced">MON ESPACE</span>
      <a routerLink="/cart" routerLinkActive="active">
        <i class="pi pi-shopping-bag" aria-hidden="true"></i><span>Panier</span>
      </a>
      <a routerLink="/wishlist" routerLinkActive="active">
        <i class="pi pi-heart" aria-hidden="true"></i><span>Liste d’envies</span>
      </a>
      <a routerLink="/contact" routerLinkActive="active">
        <i class="pi pi-envelope" aria-hidden="true"></i><span>Contact</span>
      </a>
    </nav>
  `,
  styles: [`
    :host {
      display: block;
    }

    .side-nav {
      display: flex;
      flex-direction: column;
      gap: 0.3rem;
    }

    .nav-caption {
      margin: 0.45rem 0.75rem 0.6rem;
      color: #a0a5ae;
      font-size: 0.63rem;
      font-weight: 750;
      letter-spacing: 0.14em;
    }

    .nav-caption-spaced {
      margin-top: 1.65rem;
    }

    a {
      display: flex;
      min-height: 44px;
      align-items: center;
      gap: 0.8rem;
      padding: 0 0.8rem;
      border-radius: 9px;
      color: #687180;
      font-size: 0.88rem;
      font-weight: 550;
      text-decoration: none;
      transition: background 150ms ease, color 150ms ease;
    }

    a i {
      width: 18px;
      color: #9a6b60;
      font-size: 0.95rem;
      text-align: center;
    }

    a:hover {
      background: #fbf3ee;
      color: #9e332d;
    }

    a.active {
      background: linear-gradient(100deg, #f8e9df, #fff4dc);
      color: #a8322c;
      font-weight: 700;
    }

    a.active i {
      color: #bb3d32;
    }
  `],
})
export class PanelMenuComponent {}
