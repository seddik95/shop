import { CurrencyPipe } from "@angular/common";
import { HttpErrorResponse } from "@angular/common/http";
import { Component, OnInit, computed, inject, signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { AuthService } from "app/auth/auth.service";
import { CartService } from "app/cart/cart.service";
import { Product } from "app/products/data-access/product.model";
import { WishlistService } from "app/wishlist/wishlist.service";
import { ProductsService } from "app/products/data-access/products.service";
import { ProductFormComponent } from "app/products/ui/product-form/product-form.component";
import { ButtonModule } from "primeng/button";
import { DataViewModule } from "primeng/dataview";
import { DialogModule } from "primeng/dialog";
import { DropdownModule } from "primeng/dropdown";
import { InputTextModule } from "primeng/inputtext";
import { TagModule } from "primeng/tag";

const emptyProduct: Product = {
  id: 0, code: "", name: "", description: "", image: "", category: "", price: 0, quantity: 0,
  internalReference: "", shellId: 0, inventoryStatus: "INSTOCK", rating: 0, createdAt: 0, updatedAt: 0,
};

@Component({
  selector: "app-product-list",
  templateUrl: "./product-list.component.html",
  styleUrls: ["./product-list.component.scss"],
  standalone: true,
  imports: [
    DataViewModule, ButtonModule, DialogModule, ProductFormComponent, FormsModule,
    InputTextModule, DropdownModule, TagModule, CurrencyPipe,
  ],
})
export class ProductListComponent implements OnInit {
  private readonly productsService = inject(ProductsService);
  public readonly cart = inject(CartService);
  public readonly auth = inject(AuthService);
  public readonly wishlist = inject(WishlistService);

  public readonly products = this.productsService.products;

  // filtering
  public readonly search = signal("");
  public readonly category = signal<string | null>(null);
  public readonly categories = computed(() =>
    [...new Set(this.products().map((p) => p.category))].filter(Boolean).map((c) => ({ label: c, value: c })),
  );
  public readonly filtered = computed(() => {
    const q = this.search().trim().toLowerCase();
    const cat = this.category();
    return this.products().filter(
      (p) => (!cat || p.category === cat) && (!q || `${p.name} ${p.description} ${p.code}`.toLowerCase().includes(q)),
    );
  });

  public isDialogVisible = false;
  public isCreation = false;
  public readonly editedProduct = signal<Product>(emptyProduct);
  public readonly saveError = signal<string | null>(null);

  ngOnInit() {
    this.productsService.get().subscribe();
  }

  public severity(status: Product["inventoryStatus"]) {
    return status === "INSTOCK" ? "success" : status === "LOWSTOCK" ? "warning" : "danger";
  }

  public onCreate() {
    this.isCreation = true;
    this.isDialogVisible = true;
    this.editedProduct.set(emptyProduct);
    this.saveError.set(null);
  }

  public onUpdate(product: Product) {
    this.isCreation = false;
    this.isDialogVisible = true;
    this.editedProduct.set(product);
    this.saveError.set(null);
  }

  public onDelete(product: Product) {
    this.productsService.delete(product.id).subscribe();
  }

  public onSave(product: Product) {
    this.saveError.set(null);
    (this.isCreation ? this.productsService.create(product) : this.productsService.update(product)).subscribe({
      next: () => this.closeDialog(),
      error: (error: HttpErrorResponse) => {
        this.saveError.set(
          error.status === 401
            ? "Votre session a expiré. Reconnectez-vous puis réessayez."
            : error.status === 403
              ? "Seul un administrateur peut créer ou modifier un produit."
              : error.status === 400
                ? "Les informations du produit sont invalides. Vérifiez les champs."
                : "Impossible d’enregistrer le produit. Réessayez.",
        );
      },
    });
  }

  public onCancel() {
    this.closeDialog();
  }

  private closeDialog() {
    this.isDialogVisible = false;
  }
}
