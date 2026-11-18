import { Component, inject } from "@angular/core";
import { RouterModule } from "@angular/router";
import { AuthService } from "./auth/auth.service";
import { CartService } from "./cart/cart.service";
import { BadgeModule } from "primeng/badge";
import { ButtonModule } from "primeng/button";
import { SplitterModule } from "primeng/splitter";
import { PanelMenuComponent } from "./shared/ui/panel-menu/panel-menu.component";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.scss"],
  standalone: true,
  imports: [RouterModule, SplitterModule, PanelMenuComponent, BadgeModule, ButtonModule],
})
export class AppComponent {
  title = "ALTEN SHOP";
  public readonly cart = inject(CartService);
  public readonly auth = inject(AuthService);
}
