import { Routes } from "@angular/router";
import { authGuard } from "./auth/auth.guard";
import { LoginComponent } from "./auth/login.component";
import { CartComponent } from "./cart/cart.component";
import { ContactComponent } from "./contact/contact.component";
import { WishlistComponent } from "./wishlist/wishlist.component";
import { HomeComponent } from "./shared/features/home/home.component";

export const APP_ROUTES: Routes = [
  { path: "login", component: LoginComponent },
  { path: "home", component: HomeComponent },
  {
    path: "products",
    canActivate: [authGuard],
    loadChildren: () => import("./products/products.routes").then((m) => m.PRODUCTS_ROUTES),
  },
  { path: "cart", component: CartComponent, canActivate: [authGuard] },
  { path: "wishlist", component: WishlistComponent, canActivate: [authGuard] },
  { path: "contact", component: ContactComponent },
  { path: "", redirectTo: "home", pathMatch: "full" },
];
